package com.scouter.cobblemonoutbreaks.registries;

import com.scouter.cobblemonoutbreaks.algorithms.particle.CustomParticleAlgorithm;
import com.scouter.cobblemonoutbreaks.algorithms.particle.DebugParticleAlgorithm;
import com.scouter.cobblemonoutbreaks.algorithms.particle.MovingCustomParticleAlgorithm;
import com.scouter.cobblemonoutbreaks.algorithms.particle.PokeballParticleAlgorithm;
import com.scouter.cobblemonoutbreaks.data.ParticleSpawninglAlgorithmType;
import net.minecraft.core.Registry;

import static com.scouter.cobblemonoutbreaks.CobblemonOutbreaks.prefix;

public class ParticleSpawningAlgorithmRegistry {

    public static final ParticleSpawninglAlgorithmType<?>  CUSTOM = registerParticleSpawningAlgorithmType("custom_particle", CustomParticleAlgorithm.TYPE);
    public static final ParticleSpawninglAlgorithmType<?>  MOVING = registerParticleSpawningAlgorithmType("moving_custom_particle", MovingCustomParticleAlgorithm.TYPE);
    public static final ParticleSpawninglAlgorithmType<?> POKEBALL = registerParticleSpawningAlgorithmType("pokeball_particle", PokeballParticleAlgorithm.TYPE);
    public static final ParticleSpawninglAlgorithmType<?> DEBUG = registerParticleSpawningAlgorithmType("debug_particle", DebugParticleAlgorithm.TYPE);
    private static ParticleSpawninglAlgorithmType<?> registerParticleSpawningAlgorithmType(String name, ParticleSpawninglAlgorithmType<?> type) {
        return Registry.register(CORegistries.PARTICLE_SPAWNING_ALGORITHM_TYPE_SERIALIZER, prefix(name), type);
    }

    public static void register()
    {
    }
}
