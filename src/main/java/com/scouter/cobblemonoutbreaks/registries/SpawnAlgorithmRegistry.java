package com.scouter.cobblemonoutbreaks.registries;

import com.scouter.cobblemonoutbreaks.CobblemonOutbreaks;
import com.scouter.cobblemonoutbreaks.algorithms.spawning.ClusteredAlgorithm;
import com.scouter.cobblemonoutbreaks.algorithms.spawning.InwardSpiralAlgorithm;
import com.scouter.cobblemonoutbreaks.algorithms.spawning.OpenFieldAlgorithm;
import com.scouter.cobblemonoutbreaks.data.SpawnAlgorithmType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SpawnAlgorithmRegistry {

    public static final DeferredRegister<SpawnAlgorithmType<?>> SPAWN_ALGORITHM_SERIALIZER = DeferredRegister.create(CORegistries.Keys.SPAWN_ALGORITHM_TYPE_SERIALIZERS, CobblemonOutbreaks.MODID);
    public static final DeferredHolder<SpawnAlgorithmType<?>, SpawnAlgorithmType<InwardSpiralAlgorithm>> INWARD_SPIRAL = SPAWN_ALGORITHM_SERIALIZER.register("inward_spiral", () -> InwardSpiralAlgorithm.TYPE);
    public static final DeferredHolder<SpawnAlgorithmType<?>, SpawnAlgorithmType<OpenFieldAlgorithm>> OPEN_FIELD = SPAWN_ALGORITHM_SERIALIZER.register("open_field", () -> OpenFieldAlgorithm.TYPE);
    public static final DeferredHolder<SpawnAlgorithmType<?>, SpawnAlgorithmType<ClusteredAlgorithm>> CLUSTERED = SPAWN_ALGORITHM_SERIALIZER.register("clustered", () -> ClusteredAlgorithm.TYPE);

}
