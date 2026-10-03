package com.gnomos.client;

import com.gnomos.ModItems;
import com.gnomos.entity.GnomeEntity;
import com.gnomos.entity.GnomeTrades;
import com.gnomos.network.GnomosNetwork;
import com.gnomos.network.GnomosNetwork.GnomeActionPacket;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;

/** Pantalla de conversación con un gnomo: charla por temas y trueques. */
public class GnomeDialogueScreen extends Screen {
    private static final int PANEL_W = 300;
    private static final int PANEL_H = 196;

    private final GnomeEntity gnome;
    private final int entityId;
    private final RandomSource random = RandomSource.create();
    private Component currentText;
    private boolean tradeMode;
    private int left;
    private int top;

    public static void open(GnomeEntity gnome) {
        Minecraft.getInstance().setScreen(new GnomeDialogueScreen(gnome));
    }

    public GnomeDialogueScreen(GnomeEntity gnome) {
        super(Component.literal(gnome.getGnomeName()));
        this.gnome = gnome;
        this.entityId = gnome.getId();
        this.currentText = greeting();
    }

    private boolean wearingHat() {
        Player player = Minecraft.getInstance().player;
        return player != null && player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.GNOME_HAT.get());
    }

    private Component greeting() {
        return wearingHat() ? Component.translatable("dialogue.gnomos.hat") : line("greet");
    }

    private Component line(String topic) {
        return Component.translatable("dialogue.gnomos." + gnome.getProfession().getName() + "." + topic + "."
                + (1 + random.nextInt(2)));
    }

    @Override
    protected void init() {
        rebuild();
    }

    private void addButton(int x, int y, int width, String key, Button.OnPress onPress) {
        this.addRenderableWidget(Button.builder(Component.translatable("gui.gnomos.topic." + key), onPress)
                .bounds(x, y, width, 20).build());
    }

    private void rebuild() {
        this.clearWidgets();
        this.left = (this.width - PANEL_W) / 2;
        this.top = (this.height - PANEL_H) / 2;
        int x = left + 12;
        int fullWidth = PANEL_W - 24;
        int half = (fullWidth - 4) / 2;
        int y = top + 92;

        if (!tradeMode) {
            addButton(x, y, half, "work", b -> currentText = line("work"));
            addButton(x + half + 4, y, half, "advice", b -> currentText = line("advice"));
            addButton(x, y + 24, half, "rumors", b -> currentText = line("rumors"));
            addButton(x + half + 4, y + 24, half, "trade", b -> {
                tradeMode = true;
                currentText = Component.translatable("gui.gnomos.trade_prompt");
                rebuild();
            });
            addButton(x, y + 48, fullWidth, "bye", b -> this.onClose());
        } else {
            List<GnomeTrades.Trade> trades = GnomeTrades.forProfession(gnome.getProfession());
            for (int i = 0; i < trades.size(); i++) {
                final int index = i;
                final GnomeTrades.Trade trade = trades.get(i);
                this.addRenderableWidget(Button.builder(tradeLabel(trade), b -> doTrade(index, trade))
                        .bounds(x, y + 24 * i, fullWidth, 20).build());
            }
            addButton(x, y + 24 * trades.size(), fullWidth, "back", b -> {
                tradeMode = false;
                currentText = greeting();
                rebuild();
            });
        }
    }

    private Component tradeLabel(GnomeTrades.Trade trade) {
        int cost = GnomeTrades.getCost(Minecraft.getInstance().player, trade);
        return Component.literal(cost + "x ")
                .append(trade.cost().get().getDescription())
                .append(Component.literal("  ->  " + trade.resultCount() + "x "))
                .append(trade.result().get().getDescription());
    }

    private void doTrade(int index, GnomeTrades.Trade trade) {
        Player player = Minecraft.getInstance().player;
        boolean affordable = player != null
                && GnomeTrades.count(player, trade.cost().get()) >= GnomeTrades.getCost(player, trade);
        currentText = Component.translatable(affordable ? "gui.gnomos.trade_ok" : "gui.gnomos.trade_fail");
        GnomosNetwork.CHANNEL.sendToServer(new GnomeActionPacket(entityId, GnomeActionPacket.TRADE, index));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        graphics.fill(left - 3, top - 3, left + PANEL_W + 3, top + PANEL_H + 3, 0xFF3B2A1A);
        graphics.fill(left, top, left + PANEL_W, top + PANEL_H, 0xFFF2E6C8);

        Component title = Component.literal(gnome.getGnomeName() + " · ")
                .append(Component.translatable("profession.gnomos." + gnome.getProfession().getName()));
        graphics.drawString(this.font, title, left + 12, top + 10, 0xFF5A1E1E, false);

        int textY = top + 30;
        for (FormattedCharSequence line : this.font.split(currentText, PANEL_W - 24)) {
            graphics.drawString(this.font, line, left + 12, textY, 0xFF2B1D10, false);
            textY += 11;
        }
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void tick() {
        super.tick();
        if (gnome.isRemoved() || !gnome.isAlive()) {
            this.onClose();
        }
    }

    @Override
    public void removed() {
        GnomosNetwork.CHANNEL.sendToServer(new GnomeActionPacket(entityId, GnomeActionPacket.CLOSE, 0));
        super.removed();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
