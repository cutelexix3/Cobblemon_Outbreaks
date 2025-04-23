package com.scouter.cobblemonoutbreaks.algorithms.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.scouter.cobblemonoutbreaks.data.ParticleSpawningAlgorithm;
import com.scouter.cobblemonoutbreaks.data.ParticleSpawninglAlgorithmType;
import com.scouter.cobblemonoutbreaks.registries.ParticleSpawningAlgorithmRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;

import java.util.Collections;
import java.util.List;

public class MovingCustomParticleAlgorithm implements ParticleSpawningAlgorithm {

    private final List<OutbreakParticleData> outbreakParticleDataList;
    private final int showSpeed;
    private final BlockPos totalOffset;
    private final float xSpeed;
    private final float zSpeed;
    private final float ySpeed;

    public static final MovingCustomParticleAlgorithm DEFAULT = new MovingCustomParticleAlgorithm(Collections.emptyList(), BlockPos.ZERO, 0,0,0,0);


    public static final MapCodec<MovingCustomParticleAlgorithm> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    OutbreakParticleData.CODEC.codec().listOf().fieldOf("outbreak_particle_data").forGetter(MovingCustomParticleAlgorithm::getOutbreakParticleDataList),
                    BlockPos.CODEC.fieldOf("total_offset").forGetter(MovingCustomParticleAlgorithm::getTotalOffset),
                    Codec.INT.fieldOf("show_speed").forGetter(MovingCustomParticleAlgorithm::getShowSpeed),
                    Codec.FLOAT.fieldOf("x_speed").forGetter(MovingCustomParticleAlgorithm::getxSpeed),
                    Codec.FLOAT.fieldOf("y_speed").forGetter(MovingCustomParticleAlgorithm::getySpeed),
                    Codec.FLOAT.fieldOf("z_speed").forGetter(MovingCustomParticleAlgorithm::getzSpeed)
                    ).apply(instance, MovingCustomParticleAlgorithm::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, MovingCustomParticleAlgorithm> STREAM_CODEC = StreamCodec.composite(
            OutbreakParticleData.STREAM_CODEC.apply(ByteBufCodecs.list()), MovingCustomParticleAlgorithm::getOutbreakParticleDataList,
            BlockPos.STREAM_CODEC, MovingCustomParticleAlgorithm::getTotalOffset,
            ByteBufCodecs.INT, MovingCustomParticleAlgorithm::getShowSpeed,
            ByteBufCodecs.FLOAT, MovingCustomParticleAlgorithm::getxSpeed,
            ByteBufCodecs.FLOAT, MovingCustomParticleAlgorithm::getySpeed,
            ByteBufCodecs.FLOAT, MovingCustomParticleAlgorithm::getzSpeed,
            MovingCustomParticleAlgorithm::new
    );

    public static final ParticleSpawninglAlgorithmType<MovingCustomParticleAlgorithm> TYPE = new ParticleSpawninglAlgorithmType<MovingCustomParticleAlgorithm>() {
        @Override
        public MapCodec<MovingCustomParticleAlgorithm> mapCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MovingCustomParticleAlgorithm> streamCodec() {
            return STREAM_CODEC;
        }
    };
    
    
    public MovingCustomParticleAlgorithm(List<OutbreakParticleData> outbreakParticleDataList, BlockPos totalOffset, int showSpeed, float xSpeed, float zSpeed, float ySpeed) {
        this.outbreakParticleDataList = outbreakParticleDataList;
        this.totalOffset = totalOffset;
        this.showSpeed = showSpeed;
        this.xSpeed = xSpeed;
        this.zSpeed = zSpeed;
        this.ySpeed = ySpeed;
    }


    @Override
    public void spawnParticle(ServerLevel level, BlockPos pos, int tick) {
        if (level == null || tick % showSpeed != 0) return;
        for (OutbreakParticleData data : getOutbreakParticleDataList()) {
            BlockPos spawnPos = pos.offset(data.getOffSet());
            if (data.isRandom()) {
                double randomDoubleX = level.random.nextDouble() * (data.isNegative() ? -1 : 1);
                double randomDoubleY = level.random.nextDouble() * (data.isNegative() ? -1 : 1);
                double randomDoubleZ = level.random.nextDouble() * (data.isNegative() ? -1 : 1);
                level.sendParticles(data.getOptions(), spawnPos.getX() + randomDoubleX + (Math.sin(tick) * getxSpeed()), spawnPos.getY() + randomDoubleY + (Math.sin(tick) * getySpeed()), spawnPos.getZ() + randomDoubleZ + (Math.cos(tick) * getzSpeed()), data.getParticleCount(), data.getxOffset(), data.getyOffset(), data.getzOffset(), data.getSpeed());
            } else {
                level.sendParticles(data.getOptions(), spawnPos.getX() + (Math.sin(tick) * getxSpeed()), spawnPos.getY() + (Math.sin(tick) * getySpeed()), spawnPos.getZ() + (Math.cos(tick) * getzSpeed()), data.getParticleCount(), data.getxOffset(), data.getyOffset(), data.getzOffset(), data.getSpeed());

            }
        }
    }

    public BlockPos getTotalOffset() {
        return totalOffset;
    }

    public float getxSpeed() {
        return xSpeed;
    }

    public float getySpeed() {
        return ySpeed;
    }

    public float getzSpeed() {
        return zSpeed;
    }

    public int getShowSpeed() {
        return showSpeed;
    }

    public List<OutbreakParticleData> getOutbreakParticleDataList() {
        return outbreakParticleDataList;
    }

    @Override
    public ParticleSpawninglAlgorithmType<? extends ParticleSpawningAlgorithm> type() {
        return ParticleSpawningAlgorithmRegistry.MOVING.get();
    }
}
