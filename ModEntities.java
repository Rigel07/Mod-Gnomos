package com.gnomos;

import com.gnomos.entity.GnomeEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GnomosMod.MODID);

    public static final RegistryObject<EntityType<GnomeEntity>> GNOME = ENTITY_TYPES.register("gnome",
            () -> EntityType.Builder.of(GnomeEntity::new, MobCategory.MISC)
                    .sized(0.4F, 0.65F)
                    .clientTrackingRange(8)
                    .build(new ResourceLocation(GnomosMod.MODID, "gnome").toString()));
}
