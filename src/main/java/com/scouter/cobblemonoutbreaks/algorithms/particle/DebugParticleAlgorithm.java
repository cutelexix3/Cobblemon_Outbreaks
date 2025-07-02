package com.scouter.cobblemonoutbreaks.algorithms.particle;

import com.mojang.serialization.MapCodec;
import com.scouter.cobblemonoutbreaks.data.ParticleSpawningAlgorithm;
import com.scouter.cobblemonoutbreaks.data.ParticleSpawninglAlgorithmType;
import com.scouter.cobblemonoutbreaks.registries.ParticleSpawningAlgorithmRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;

import java.util.List;

public class DebugParticleAlgorithm implements ParticleSpawningAlgorithm {

    private final CustomParticleAlgorithm outbreakParticle;
    public static final DebugParticleAlgorithm ALGO = new DebugParticleAlgorithm();

    public static final CustomParticleAlgorithm DEBUG_PARTICLE = new CustomParticleAlgorithm(List.of(
                new OutbreakParticleData(ParticleTypes.FLAME, BlockPos.ZERO, false, false,2,0,1,0,0),
                new OutbreakParticleData(ParticleTypes.CRIT, BlockPos.ZERO, true, false,2,0,1,0,0),
                new OutbreakParticleData(ParticleTypes.CRIT, BlockPos.ZERO, true, false,2,0,1,0,0),
                new OutbreakParticleData(ParticleTypes.CRIT, BlockPos.ZERO, true, true,2,0,1,0,0),
                new OutbreakParticleData(ParticleTypes.CRIT, BlockPos.ZERO, true, true,2,0,1,0,0)
        ),
                10);

    public static final MapCodec<DebugParticleAlgorithm> CODEC = MapCodec.unit(ALGO);


    public static final StreamCodec<RegistryFriendlyByteBuf, DebugParticleAlgorithm> STREAM_CODEC = StreamCodec.unit(
            ALGO
    );

    public static final ParticleSpawninglAlgorithmType<DebugParticleAlgorithm> TYPE = new ParticleSpawninglAlgorithmType<DebugParticleAlgorithm>() {
        @Override
        public MapCodec<DebugParticleAlgorithm> mapCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DebugParticleAlgorithm> streamCodec() {
            return STREAM_CODEC;
        }
    };


    public DebugParticleAlgorithm() {
        this.outbreakParticle = DEBUG_PARTICLE;

    }


    @Override
    public void spawnParticle(ServerLevel level, BlockPos pos, int tick) {
        outbreakParticle.spawnParticle(level, pos, tick);
    }



    @Override
    public ParticleSpawninglAlgorithmType<? extends ParticleSpawningAlgorithm> type() {
        return ParticleSpawningAlgorithmRegistry.DEBUG;
    }
}
