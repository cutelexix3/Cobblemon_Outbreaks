package com.scouter.cobblemonoutbreaks.setup;

import com.scouter.cobblemonoutbreaks.registries.LevelAlgorithmRegistry;
import com.scouter.cobblemonoutbreaks.registries.ParticleSpawningAlgorithmRegistry;
import com.scouter.cobblemonoutbreaks.registries.SpawnAlgorithmRegistry;

public class Registration {
    public static void init(){
      //  COEntity.ENTITY_TYPES();

        LevelAlgorithmRegistry.register();
        ParticleSpawningAlgorithmRegistry.register();
        SpawnAlgorithmRegistry.register();
    }

}
