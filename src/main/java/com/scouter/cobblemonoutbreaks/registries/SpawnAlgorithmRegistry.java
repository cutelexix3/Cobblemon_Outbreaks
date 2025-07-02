package com.scouter.cobblemonoutbreaks.registries;

import com.scouter.cobblemonoutbreaks.algorithms.spawning.ClusteredAlgorithm;
import com.scouter.cobblemonoutbreaks.algorithms.spawning.InwardSpiralAlgorithm;
import com.scouter.cobblemonoutbreaks.algorithms.spawning.OpenFieldAlgorithm;
import com.scouter.cobblemonoutbreaks.data.SpawnAlgorithmType;
import net.minecraft.core.Registry;

import static com.scouter.cobblemonoutbreaks.CobblemonOutbreaks.prefix;

public class SpawnAlgorithmRegistry {

    public static final SpawnAlgorithmType<?> INWARD_SPIRAL = registerSpawningAlgorithmType("inward_spiral", InwardSpiralAlgorithm.TYPE);
    public static final SpawnAlgorithmType<?> OPEN_FIELD = registerSpawningAlgorithmType("open_field", OpenFieldAlgorithm.TYPE);
    public static final SpawnAlgorithmType<?> CLUSTERED = registerSpawningAlgorithmType("clustered", ClusteredAlgorithm.TYPE);
    private static SpawnAlgorithmType<?> registerSpawningAlgorithmType(String name, SpawnAlgorithmType<?> type) {
        return Registry.register(CORegistries.SPAWN_ALGORITHM_TYPE_SERIALIZER, prefix(name), type);
    }

    public static void register()
    {
    }
}
