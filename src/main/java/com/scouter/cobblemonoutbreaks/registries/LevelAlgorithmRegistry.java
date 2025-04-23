package com.scouter.cobblemonoutbreaks.registries;

import com.scouter.cobblemonoutbreaks.CobblemonOutbreaks;
import com.scouter.cobblemonoutbreaks.algorithms.level.MinMaxAlgorithm;
import com.scouter.cobblemonoutbreaks.algorithms.level.RandomAlgorithm;
import com.scouter.cobblemonoutbreaks.algorithms.level.RandomScaledAlgorithm;
import com.scouter.cobblemonoutbreaks.algorithms.level.ScaledAlgorithm;
import com.scouter.cobblemonoutbreaks.data.LevelAlgorithmType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LevelAlgorithmRegistry {

    public static final DeferredRegister<LevelAlgorithmType<?>> SPAWN_ALGORITHM_SERIALIZER = DeferredRegister.create(CORegistries.Keys.LEVEL_ALGORITHM_TYPE_SERIALIZERS, CobblemonOutbreaks.MODID);
    public static final DeferredHolder<LevelAlgorithmType<?>, LevelAlgorithmType<RandomAlgorithm>> RANDOM = SPAWN_ALGORITHM_SERIALIZER.register("random", () -> RandomAlgorithm.TYPE);
    public static final DeferredHolder<LevelAlgorithmType<?>, LevelAlgorithmType<ScaledAlgorithm>> SCALED = SPAWN_ALGORITHM_SERIALIZER.register("scaled", () -> ScaledAlgorithm.TYPE);
    public static final DeferredHolder<LevelAlgorithmType<?>, LevelAlgorithmType<RandomScaledAlgorithm>> RANDOM_SCALED = SPAWN_ALGORITHM_SERIALIZER.register("random_scaled", () -> RandomScaledAlgorithm.TYPE);

    public static final DeferredHolder<LevelAlgorithmType<?>, LevelAlgorithmType<MinMaxAlgorithm>> MIN_MAX = SPAWN_ALGORITHM_SERIALIZER.register("min_max", () -> MinMaxAlgorithm.TYPE);

}
