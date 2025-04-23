package com.scouter.cobblemonoutbreaks.registries;

import com.scouter.cobblemonoutbreaks.CobblemonOutbreaks;
import com.scouter.cobblemonoutbreaks.algorithms.particle.CustomParticleAlgorithm;
import com.scouter.cobblemonoutbreaks.algorithms.particle.DebugParticleAlgorithm;
import com.scouter.cobblemonoutbreaks.algorithms.particle.MovingCustomParticleAlgorithm;
import com.scouter.cobblemonoutbreaks.algorithms.particle.PokeballParticleAlgorithm;
import com.scouter.cobblemonoutbreaks.data.ParticleSpawninglAlgorithmType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ParticleSpawningAlgorithmRegistry {

    public static final DeferredRegister<ParticleSpawninglAlgorithmType<?>> SPAWN_ALGORITHM_SERIALIZER = DeferredRegister.create(CORegistries.Keys.PARTICLE_SPAWNING_ALGORITHM_TYPE_SERIALIZERS, CobblemonOutbreaks.MODID);
    public static final DeferredHolder<ParticleSpawninglAlgorithmType<?>, ParticleSpawninglAlgorithmType<CustomParticleAlgorithm>> CUSTOM = SPAWN_ALGORITHM_SERIALIZER.register("custom_particle", () -> CustomParticleAlgorithm.TYPE);
    public static final DeferredHolder<ParticleSpawninglAlgorithmType<?>, ParticleSpawninglAlgorithmType<MovingCustomParticleAlgorithm>> MOVING = SPAWN_ALGORITHM_SERIALIZER.register("moving_custom_particle", () -> MovingCustomParticleAlgorithm.TYPE);
    public static final DeferredHolder<ParticleSpawninglAlgorithmType<?>, ParticleSpawninglAlgorithmType<PokeballParticleAlgorithm>> POKEBALL = SPAWN_ALGORITHM_SERIALIZER.register("pokeball_particle", () -> PokeballParticleAlgorithm.TYPE);
    public static final DeferredHolder<ParticleSpawninglAlgorithmType<?>, ParticleSpawninglAlgorithmType<DebugParticleAlgorithm>> DEBUG = SPAWN_ALGORITHM_SERIALIZER.register("debug_particle", () -> DebugParticleAlgorithm.TYPE);

}
