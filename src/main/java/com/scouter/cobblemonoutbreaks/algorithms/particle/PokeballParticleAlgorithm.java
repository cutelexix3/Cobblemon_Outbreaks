package com.scouter.cobblemonoutbreaks.algorithms.particle;

import com.mojang.serialization.MapCodec;
import com.scouter.cobblemonoutbreaks.data.ParticleSpawningAlgorithm;
import com.scouter.cobblemonoutbreaks.data.ParticleSpawninglAlgorithmType;
import com.scouter.cobblemonoutbreaks.registries.ParticleSpawningAlgorithmRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;

public class PokeballParticleAlgorithm implements ParticleSpawningAlgorithm {


    private final MovingCustomParticleAlgorithm pokeballAlgo;

    public static final PokeballParticleAlgorithm ALGO = new PokeballParticleAlgorithm();

    public static final MapCodec<PokeballParticleAlgorithm> CODEC = MapCodec.unit(ALGO);


    public static final StreamCodec<RegistryFriendlyByteBuf, PokeballParticleAlgorithm> STREAM_CODEC = StreamCodec.unit(
            ALGO
    );
    public static final ParticleSpawninglAlgorithmType<PokeballParticleAlgorithm> TYPE = new ParticleSpawninglAlgorithmType<PokeballParticleAlgorithm>() {
        @Override
        public MapCodec<PokeballParticleAlgorithm> mapCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PokeballParticleAlgorithm> streamCodec() {
            return STREAM_CODEC;
        }
    };
    
    //todo make this in a pokeball shape
    public PokeballParticleAlgorithm() {
        this.pokeballAlgo = MovingCustomParticleAlgorithm.DEFAULT;
    }

    @Override
    public void spawnParticle(ServerLevel level, BlockPos pos, int tick) {
        if(level == null) return;
        pokeballAlgo.spawnParticle(level, pos,tick);
    }

    @Override
    public ParticleSpawninglAlgorithmType<? extends ParticleSpawningAlgorithm> type() {
        return ParticleSpawningAlgorithmRegistry.POKEBALL;
    }
}
