package com.gnomos;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GnomosMod.MODID);
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GnomosMod.MODID);

    /** Material del gorro de gnomo (solo casco). Textura: textures/models/armor/gnome_layer_1.png */
    public enum GnomeArmorMaterial implements ArmorMaterial {
        GNOME;

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return 60;
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return type == ArmorItem.Type.HELMET ? 1 : 0;
        }

        @Override
        public int getEnchantmentValue() {
            return 15;
        }

        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_LEATHER;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(Items.RED_WOOL);
        }

        @Override
        public String getName() {
            return "gnomos:gnome";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    }

    public static final RegistryObject<Item> GNOME_HAT = ITEMS.register("gnome_hat",
            () -> new ArmorItem(GnomeArmorMaterial.GNOME, ArmorItem.Type.HELMET, new Item.Properties()));

    public static final RegistryObject<Item> GNOME_BREAD = ITEMS.register("gnome_bread",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(6).saturationMod(0.7F).build())));

    public static final RegistryObject<Item> GLOWCAP = ITEMS.register("glowcap",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(3).saturationMod(0.4F)
                    .effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 600, 0), 1.0F)
                    .alwaysEat().build())));

    public static final RegistryObject<Item> GNOME_SPAWN_EGG = ITEMS.register("gnome_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.GNOME, 0xC83232, 0x3A5AA0, new Item.Properties()));

    public static final RegistryObject<CreativeModeTab> GNOMOS_TAB = TABS.register("gnomos",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.gnomos"))
                    .icon(() -> new ItemStack(GNOME_HAT.get()))
                    .displayItems((parameters, output) ->
                            ITEMS.getEntries().forEach(item -> output.accept(item.get())))
                    .build());
}
