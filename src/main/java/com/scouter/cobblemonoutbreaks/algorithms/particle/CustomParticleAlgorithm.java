package com.scouter.cobblemonoutbreaks.algorithms.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.scouter.cobblemonoutbreaks.data.ParticleSpawningAlgorithm;
import com.scouter.cobblemonoutbreaks.data.ParticleSpawninglAlgorithmType;
import com.scouter.cobblemonoutbreaks.registries.ParticleSpawningAlgorithmRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;

import java.util.List;

public class CustomParticleAlgorithm implements ParticleSpawningAlgorithm {

    private final List<OutbreakParticleData> outbreakParticleDataList;
    private final int showSpeed;

    public static final CustomParticleAlgorithm DEBUG_PARTICLE = new CustomParticleAlgorithm(List.of(
                new OutbreakParticleData(ParticleTypes.FLAME, BlockPos.ZERO, false, false,2,0,1,0,0),
                new OutbreakParticleData(ParticleTypes.CRIT, BlockPos.ZERO, true, false,2,0,1,0,0),
                new OutbreakParticleData(ParticleTypes.CRIT, BlockPos.ZERO, true, false,2,0,1,0,0),
                new OutbreakParticleData(ParticleTypes.CRIT, BlockPos.ZERO, true, true,2,0,1,0,0),
                new OutbreakParticleData(ParticleTypes.CRIT, BlockPos.ZERO, true, true,2,0,1,0,0)
        ),
                10);

    public static final MapCodec<CustomParticleAlgorithm> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    OutbreakParticleData.CODEC.codec().listOf().fieldOf("outbreak_particle_data").forGetter(CustomParticleAlgorithm::getOutbreakParticleDataList),
                    Codec.INT.fieldOf("show_speed").forGetter(CustomParticleAlgorithm::getShowSpeed)
            ).apply(instance, CustomParticleAlgorithm::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, CustomParticleAlgorithm> STREAM_CODEC = StreamCodec.composite(
            OutbreakParticleData.STREAM_CODEC.apply(ByteBufCodecs.list()), CustomParticleAlgorithm::getOutbreakParticleDataList,
            ByteBufCodecs.INT, CustomParticleAlgorithm::getShowSpeed,
            CustomParticleAlgorithm::new
    );

    public static final ParticleSpawninglAlgorithmType<CustomParticleAlgorithm> TYPE = new ParticleSpawninglAlgorithmType<CustomParticleAlgorithm>() {
        @Override
        public MapCodec<CustomParticleAlgorithm> mapCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CustomParticleAlgorithm> streamCodec() {
            return STREAM_CODEC;
        }
    };


    public CustomParticleAlgorithm(List<OutbreakParticleData> outbreakParticleDataList, int showSpeed) {
        this.outbreakParticleDataList = outbreakParticleDataList;
        this.showSpeed = showSpeed;
    }


    @Override
    public void spawnParticle(ServerLevel level, BlockPos pos, int tick) {
        if(level == null || tick % showSpeed != 0) return;
        for(OutbreakParticleData data : getOutbreakParticleDataList()) {
            data.spawnParticle(level, pos, tick);
        }
    }

    public List<OutbreakParticleData> getOutbreakParticleDataList() {
        return outbreakParticleDataList;
    }

    public int getShowSpeed() {
        return showSpeed;
    }

    @Override
    public ParticleSpawninglAlgorithmType<? extends ParticleSpawningAlgorithm> type() {
        return ParticleSpawningAlgorithmRegistry.CUSTOM.get();
    }
}
