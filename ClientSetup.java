package com.gnomos.client;

import com.gnomos.GnomosMod;
import com.gnomos.ModBlocks;
import com.gnomos.ModEntities;
import com.gnomos.block.GnomeStatueBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GnomosMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(GnomeModel.LAYER, GnomeModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.GNOME.get(), GnomeRenderer::new);
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> {
            int color = state.getValue(GnomeStatueBlock.COLOR);
            return tintIndex == 0 ? GnomeStatueBlock.HAT_COLORS[color] : GnomeStatueBlock.TUNIC_COLORS[color];
        }, ModBlocks.GNOME_STATUE.get());
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) ->
                tintIndex == 0 ? GnomeStatueBlock.HAT_COLORS[0] : GnomeStatueBlock.TUNIC_COLORS[0],
                ModBlocks.GNOME_STATUE.get());
    }
}
