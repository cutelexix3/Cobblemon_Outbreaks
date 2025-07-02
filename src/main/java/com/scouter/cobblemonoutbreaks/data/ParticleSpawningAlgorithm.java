package com.scouter.cobblemonoutbreaks.data;

import com.mojang.serialization.Codec;
import com.scouter.cobblemonoutbreaks.registries.CORegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;

public interface ParticleSpawningAlgorithm {

    Codec<ParticleSpawningAlgorithm> DIRECT_CODEC = CORegistries.PARTICLE_SPAWNING_ALGORITHM_TYPE_SERIALIZER.byNameCodec()
            .dispatch(ParticleSpawningAlgorithm::type, ParticleSpawninglAlgorithmType::mapCodec);

    StreamCodec<RegistryFriendlyByteBuf, ParticleSpawningAlgorithm> DIRECT_STREAM_CODEC =
            ByteBufCodecs.registry(CORegistries.PARTICLE_SPAWNING_ALGORITHM_TYPE_SERIALIZER.key())
                    .dispatch(ParticleSpawningAlgorithm::type, ParticleSpawninglAlgorithmType::streamCodec);
    
    
    
    void spawnParticle(ServerLevel level, BlockPos pos, int tick);
    ParticleSpawninglAlgorithmType<? extends ParticleSpawningAlgorithm> type();

}
