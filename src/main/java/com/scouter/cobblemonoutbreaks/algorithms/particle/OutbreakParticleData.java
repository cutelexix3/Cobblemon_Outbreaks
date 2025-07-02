package com.scouter.cobblemonoutbreaks.algorithms.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.scouter.cobblemonoutbreaks.codec.OutbreaksStreamCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;

public class OutbreakParticleData {

    public static final MapCodec<OutbreakParticleData> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ParticleTypes.CODEC.fieldOf("particle")
                            .forGetter(OutbreakParticleData::getOptions),
                    BlockPos.CODEC.fieldOf("offset")
                            .forGetter(OutbreakParticleData::getOffSet),
                    Codec.BOOL.optionalFieldOf("random", false)
                            .forGetter(OutbreakParticleData::isRandom),
                    Codec.BOOL.optionalFieldOf("negative", false)
                            .forGetter(OutbreakParticleData::isNegative),
                    Codec.INT.optionalFieldOf("particle_count", 1)
                            .forGetter(OutbreakParticleData::getParticleCount),
                    Codec.INT.optionalFieldOf("x_offset", 0)
                            .forGetter(OutbreakParticleData::getxOffset),
                    Codec.INT.optionalFieldOf("y_offset", 0)
                            .forGetter(OutbreakParticleData::getyOffset),
                    Codec.INT.optionalFieldOf("z_offset", 0)
                            .forGetter(OutbreakParticleData::getzOffset),
                    Codec.INT.optionalFieldOf("speed", 0)
                            .forGetter(OutbreakParticleData::getSpeed)
            ).apply(instance, OutbreakParticleData::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, OutbreakParticleData> STREAM_CODEC = OutbreaksStreamCodecs.composite(
            ParticleTypes.STREAM_CODEC, OutbreakParticleData::getOptions,
            BlockPos.STREAM_CODEC, OutbreakParticleData::getOffSet,
            ByteBufCodecs.BOOL, OutbreakParticleData::isRandom,
            ByteBufCodecs.BOOL, OutbreakParticleData::isNegative,
            ByteBufCodecs.INT, OutbreakParticleData::getParticleCount,
            ByteBufCodecs.INT, OutbreakParticleData::getxOffset,
            ByteBufCodecs.INT, OutbreakParticleData::getyOffset,
            ByteBufCodecs.INT, OutbreakParticleData::getzOffset,
            ByteBufCodecs.INT, OutbreakParticleData::getSpeed,
            OutbreakParticleData::new
    );
    
    private final ParticleOptions options;
    private final BlockPos offSet;
    private final boolean random;
    private final boolean negative;
    private final int particleCount;
    private final int xOffset;
    private final int yOffset;
    private final int zOffset;
    private final int speed;

    public OutbreakParticleData(ParticleOptions options, BlockPos offSet, boolean random, boolean negative, int particleCount, int xOffset, int yOffset, int zOffset, int speed) {
        this.options = options;
        this.offSet = offSet;
        this.random = random;
        this.negative = negative;
        this.particleCount = particleCount;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
        this.speed = speed;
    }


    public void spawnParticle(ServerLevel level, BlockPos pos, int tick) {
        if (level == null) return;
        BlockPos spawnPos = pos.offset(offSet);
        if (random) {
            double randomDoubleX = level.random.nextDouble() * (isNegative() ? -1 : 1);
            double randomDoubleY = level.random.nextDouble() * (isNegative() ? -1 : 1);
            double randomDoubleZ = level.random.nextDouble() * (isNegative() ? -1 : 1);
             level.sendParticles(options, spawnPos.getX() + randomDoubleX, spawnPos.getY() + randomDoubleY , spawnPos.getZ() + randomDoubleZ, particleCount, xOffset, yOffset, zOffset, speed);
        } else {
            level.sendParticles(options, spawnPos.getX(), spawnPos.getY()  , spawnPos.getZ(), particleCount, xOffset, yOffset, zOffset, speed);

        }
    }


    public boolean isNegative() {
        return negative;
    }

    public boolean isRandom() {
        return random;
    }

    public BlockPos getOffSet() {
        return offSet;
    }

    public ParticleOptions getOptions() {
        return options;
    }

    public int getParticleCount() {
        return particleCount;
    }

    public int getSpeed() {
        return speed;
    }

    public int getxOffset() {
        return xOffset;
    }

    public int getyOffset() {
        return yOffset;
    }

    public int getzOffset() {
        return zOffset;
    }
}
