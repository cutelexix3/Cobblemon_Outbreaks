package com.scouter.cobblemonoutbreaks.registries;

import com.scouter.cobblemonoutbreaks.algorithms.level.MinMaxAlgorithm;
import com.scouter.cobblemonoutbreaks.algorithms.level.RandomAlgorithm;
import com.scouter.cobblemonoutbreaks.algorithms.level.RandomScaledAlgorithm;
import com.scouter.cobblemonoutbreaks.algorithms.level.ScaledAlgorithm;
import com.scouter.cobblemonoutbreaks.data.LevelAlgorithmType;
import net.minecraft.core.Registry;

import static com.scouter.cobblemonoutbreaks.CobblemonOutbreaks.prefix;

public class LevelAlgorithmRegistry {

    public static final LevelAlgorithmType<?> RANDOM = registerLevelAlgorithmType("random", RandomAlgorithm.TYPE);
    public static final LevelAlgorithmType<?> SCALED = registerLevelAlgorithmType("scaled", ScaledAlgorithm.TYPE);
    public static final LevelAlgorithmType<?> RANDOM_SCALED = registerLevelAlgorithmType("random_scaled", RandomScaledAlgorithm.TYPE);

    public static final LevelAlgorithmType<?> MIN_MAX = registerLevelAlgorithmType("min_max",  MinMaxAlgorithm.TYPE);


    private static LevelAlgorithmType<?> registerLevelAlgorithmType(String name, LevelAlgorithmType<?> type) {
        return Registry.register(CORegistries.LEVEL_ALGORITHM_TYPE_SERIALIZER, prefix(name), type);
    }

    public static void register()
    {
    }
}
