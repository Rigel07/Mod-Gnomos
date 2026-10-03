package com.gnomos.network;

import com.gnomos.GnomosMod;
import com.gnomos.entity.GnomeEntity;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class GnomosNetwork {
    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(GnomosMod.MODID, "main"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    public static void register() {
        CHANNEL.registerMessage(0, GnomeActionPacket.class,
                GnomeActionPacket::encode, GnomeActionPacket::decode, GnomeActionPacket::handle);
    }

    /** Cliente -> servidor: cerrar el diálogo o aceptar un trato. */
    public static class GnomeActionPacket {
        public static final int CLOSE = 0;
        public static final int TRADE = 1;

        private final int entityId;
        private final int action;
        private final int index;

        public GnomeActionPacket(int entityId, int action, int index) {
            this.entityId = entityId;
            this.action = action;
            this.index = index;
        }

        public static void encode(GnomeActionPacket msg, FriendlyByteBuf buf) {
            buf.writeVarInt(msg.entityId);
            buf.writeByte(msg.action);
            buf.writeByte(msg.index);
        }

        public static GnomeActionPacket decode(FriendlyByteBuf buf) {
            return new GnomeActionPacket(buf.readVarInt(), buf.readByte(), buf.readByte());
        }

        public static void handle(GnomeActionPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                ServerPlayer player = context.getSender();
                if (player == null) {
                    return;
                }
                Entity entity = player.level().getEntity(msg.entityId);
                if (!(entity instanceof GnomeEntity gnome) || player.distanceToSqr(gnome) > 100.0D) {
                    return;
                }
                if (msg.action == CLOSE) {
                    gnome.stopTalking();
                } else if (msg.action == TRADE) {
                    gnome.tryTrade(player, msg.index);
                }
            });
            context.setPacketHandled(true);
        }
    }
}
