package com.gnomos.entity;

import com.gnomos.ModBlocks;
import com.gnomos.ModItems;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Tratos de cada profesión. El gorro de gnomo en la cabeza resta 1 al coste (mínimo 1). */
public final class GnomeTrades {
    public record Trade(Supplier<Item> cost, int costCount, Supplier<Item> result, int resultCount) {
    }

    private static final Map<GnomeProfession, List<Trade>> TRADES = new EnumMap<>(GnomeProfession.class);

    private static Trade t(Supplier<Item> cost, int costCount, Supplier<Item> result, int resultCount) {
        return new Trade(cost, costCount, result, resultCount);
    }

    static {
        TRADES.put(GnomeProfession.FARMER, List.of(
                t(() -> Items.WHEAT, 8, () -> ModItems.GNOME_BREAD.get(), 2),
                t(() -> Items.CARROT, 6, () -> Items.GOLDEN_CARROT, 1),
                t(() -> Items.EMERALD, 1, () -> ModItems.GNOME_BREAD.get(), 4)));
        TRADES.put(GnomeProfession.MINER, List.of(
                t(() -> Items.COAL, 6, () -> Items.IRON_INGOT, 2),
                t(() -> Items.IRON_INGOT, 4, () -> Items.EMERALD, 1),
                t(() -> Items.EMERALD, 6, () -> Items.DIAMOND, 1)));
        TRADES.put(GnomeProfession.BLACKSMITH, List.of(
                t(() -> Items.IRON_INGOT, 6, () -> Items.IRON_PICKAXE, 1),
                t(() -> Items.IRON_INGOT, 8, () -> Items.IRON_SWORD, 1),
                t(() -> Items.EMERALD, 2, () -> ModBlocks.GNOME_BRICKS.get().asItem(), 8)));
        TRADES.put(GnomeProfession.FORAGER, List.of(
                t(() -> Items.SWEET_BERRIES, 8, () -> ModItems.GLOWCAP.get(), 2),
                t(() -> Items.BROWN_MUSHROOM, 4, () -> Items.EMERALD, 1),
                t(() -> Items.EMERALD, 2, () -> ModItems.GLOWCAP.get(), 4)));
        TRADES.put(GnomeProfession.ELDER, List.of(
                t(() -> Items.EMERALD, 3, () -> ModItems.GNOME_HAT.get(), 1),
                t(() -> Items.EMERALD, 2, () -> ModBlocks.MUSHROOM_LAMP.get().asItem(), 2),
                t(() -> Items.EMERALD, 4, () -> ModBlocks.GNOME_STATUE.get().asItem(), 1)));
    }

    private GnomeTrades() {
    }

    public static List<Trade> forProfession(GnomeProfession profession) {
        return TRADES.get(profession);
    }

    public static int getCost(Player player, Trade trade) {
        int cost = trade.costCount();
        if (player != null && player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.GNOME_HAT.get()) && cost > 1) {
            cost--;
        }
        return cost;
    }

    public static int count(Player player, Item item) {
        int total = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(item)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    public static void remove(Player player, Item item, int amount) {
        int remaining = amount;
        for (ItemStack stack : player.getInventory().items) {
            if (remaining <= 0) {
                break;
            }
            if (stack.is(item)) {
                int take = Math.min(stack.getCount(), remaining);
                stack.shrink(take);
                remaining -= take;
            }
        }
        player.getInventory().setChanged();
    }
}
