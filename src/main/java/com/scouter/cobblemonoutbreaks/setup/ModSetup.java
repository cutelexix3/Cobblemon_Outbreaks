package com.scouter.cobblemonoutbreaks.setup;

import com.scouter.cobblemonoutbreaks.CobblemonOutbreaks;
import com.scouter.cobblemonoutbreaks.registries.CORegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber(modid = CobblemonOutbreaks.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModSetup {

    public static void init(FMLCommonSetupEvent event){
        event.enqueueWork(() -> {
        });
    }

    public static void setup(){
    }
    @SubscribeEvent
    private static void registerRegistries(NewRegistryEvent event) {
        event.register(CORegistries.SPAWN_ALGORITHM_TYPE_SERIALIZER);
        event.register(CORegistries.LEVEL_ALGORITHM_TYPE_SERIALIZER);
        event.register(CORegistries.PARTICLE_SPAWNING_ALGORITHM_TYPE_SERIALIZER);

    }
}
