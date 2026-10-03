package com.gnomos;

import com.gnomos.entity.GnomeEntity;
import com.gnomos.network.GnomosNetwork;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(GnomosMod.MODID)
public class GnomosMod {
    public static final String MODID = "gnomos";

    public GnomosMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModItems.TABS.register(modBus);
        ModEntities.ENTITY_TYPES.register(modBus);

        modBus.addListener(this::commonSetup);
        modBus.addListener(this::registerAttributes);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(GnomosNetwork::register);
    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.GNOME.get(), GnomeEntity.createAttributes().build());
    }
}
