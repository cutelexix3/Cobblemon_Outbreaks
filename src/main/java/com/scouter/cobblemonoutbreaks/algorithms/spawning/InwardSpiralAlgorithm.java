package com.scouter.cobblemonoutbreaks.algorithms.spawning;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.scouter.cobblemonoutbreaks.data.SpawnAlgorithm;
import com.scouter.cobblemonoutbreaks.data.SpawnAlgorithmType;
import com.scouter.cobblemonoutbreaks.portal.entity.OutbreakPortalEntity;
import com.scouter.cobblemonoutbreaks.registries.SpawnAlgorithmRegistry;
import com.scouter.cobblemonoutbreaks.util.AABBHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public class InwardSpiralAlgorithm implements SpawnAlgorithm {


    public static final MapCodec<InwardSpiralAlgorithm> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.doubleRange(5D,40D).optionalFieldOf("spawn_range", 15D).forGetter(InwardSpiralAlgorithm::getSpawnRange),
            Codec.doubleRange(5D,40D).optionalFieldOf("leash_range", 32D).forGetter(InwardSpiralAlgorithm::getLeashRange)
    ).apply(instance, InwardSpiralAlgorithm::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, InwardSpiralAlgorithm> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, InwardSpiralAlgorithm::getSpawnRange,
            ByteBufCodecs.DOUBLE, InwardSpiralAlgorithm::getLeashRange,
            InwardSpiralAlgorithm::new
    );

    protected double spawnRange;
    protected double leashRange;

    public InwardSpiralAlgorithm(double spawnRange, double leashRange) {
        this.spawnRange = spawnRange;
        this.leashRange = leashRange;
    }


    public double getLeashRange() {
        return leashRange;
    }

    public double getSpawnRange() {
        return spawnRange;
    }
    public static final SpawnAlgorithmType<InwardSpiralAlgorithm> TYPE = new SpawnAlgorithmType<InwardSpiralAlgorithm>() {
        @Override
        public MapCodec<InwardSpiralAlgorithm> mapCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, InwardSpiralAlgorithm> streamCodec() {
            return STREAM_CODEC;
        }
    };


    @Override
    public Vec3 spawnPosition(ServerLevel level, Vec3 pos, OutbreakPortalEntity outbreakPortal, PokemonEntity pokemon) {
        double spawnRange = getSpawnRange();

        int tries = 0;
        double x = pos.x() + (-1 + 2 * level.random.nextDouble()) * spawnRange;
        double y = pos.y() + level.random.nextInt(3) - 1;
        double z = pos.z() + (-1 + 2 * level.random.nextDouble()) * spawnRange;
        while (!level.noCollision(AABBHelper.getAABB(x,y,z, pokemon)) && tries++ < MAX_SPAWN_TRIES) {
            float scaleFactor = (MAX_SPAWN_TRIES - 1 - tries) / (float) MAX_SPAWN_TRIES;
            x = pos.x() + scaleFactor * (level.random.nextDouble() - level.random.nextDouble()) * spawnRange + 0.5D;
            y = pos.y() + scaleFactor * level.random.nextInt(3) + 1;
            z = pos.z() + scaleFactor * (level.random.nextDouble() - level.random.nextDouble()) * spawnRange + 0.5D;
        }

        while (level.getBlockState(BlockPos.containing(x, y - 1, z)).isAir() && y > level.getMinBuildHeight()) {
            y--;
        }

        while (!level.noCollision(AABBHelper.getAABB(x, y, z, pokemon))) {
            y++;
        }

        if (outbreakPortal.distanceToSqr(x, y, z) > (getLeashRange() * getLeashRange())) return null;

        if (level.noCollision(pokemon.getBoundingBox().inflate(x,y,z))) return new Vec3(x, y, z);

        return null;
    }

    @Override
    public SpawnAlgorithmType<? extends SpawnAlgorithm> type() {
        return SpawnAlgorithmRegistry.INWARD_SPIRAL.get();
    }
}
