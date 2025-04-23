package com.scouter.cobblemonoutbreaks.registries;

import com.scouter.cobblemonoutbreaks.data.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;

import static com.scouter.cobblemonoutbreaks.CobblemonOutbreaks.prefix;

public class CORegistries {
    static { init(); }

    public static final Registry<SpawnAlgorithmType<?>> SPAWN_ALGORITHM_TYPE_SERIALIZER = new RegistryBuilder<>(Keys.SPAWN_ALGORITHM_TYPE_SERIALIZERS).maxId(Integer.MAX_VALUE - 1).sync(false).create();

    public static final Registry<LevelAlgorithmType<?>> LEVEL_ALGORITHM_TYPE_SERIALIZER = new RegistryBuilder<>(Keys.LEVEL_ALGORITHM_TYPE_SERIALIZERS).maxId(Integer.MAX_VALUE - 1).sync(false).create();
    public static final Registry<ParticleSpawninglAlgorithmType<?>> PARTICLE_SPAWNING_ALGORITHM_TYPE_SERIALIZER = new RegistryBuilder<>(Keys.PARTICLE_SPAWNING_ALGORITHM_TYPE_SERIALIZERS).maxId(Integer.MAX_VALUE - 1).sync(false).create();


    public static final class Keys {

        //public static final ResourceKey<Registry<MapCodec<? extends AnimationBuilderCodec>>> ANIMATION_BUILDER_SERIALIZERS = key(prefix("animation_builder_serializer").toString());
        public static final ResourceKey<Registry<SpawnAlgorithmType<?>>> SPAWN_ALGORITHM_TYPE_SERIALIZERS = key(prefix("spawn_algorithm_type_serializer").toString());
        public static final ResourceKey<Registry<SpawnAlgorithm>> BLOCK_PATTERN_BUILDER_TYPE = key(prefix("spawn_algorithm_type").toString());

        public static final ResourceKey<Registry<LevelAlgorithmType<?>>> LEVEL_ALGORITHM_TYPE_SERIALIZERS = key(prefix("level_algorithm_type_serializer").toString());
        public static final ResourceKey<Registry<LevelAlgorithm>> LEVEL_ALGORITH_TYPE = key(prefix("level_algorithm_type").toString());


        public static final ResourceKey<Registry<ParticleSpawninglAlgorithmType<?>>> PARTICLE_SPAWNING_ALGORITHM_TYPE_SERIALIZERS = key(prefix("particle_spawning_algorithm_type_serializer").toString());
        public static final ResourceKey<Registry<ParticleSpawningAlgorithm>> PARTICLE_SPAWNING_ALGORITH_TYPE = key(prefix("particle_spawning_algorithm_type").toString());

        private static <T> ResourceKey<Registry<T>> key(String name)
        {
            return ResourceKey.createRegistryKey(ResourceLocation.parse(name));
        }
        private static void init() {}

    }

    private static void init()
    {
        Keys.init();
    }

}
