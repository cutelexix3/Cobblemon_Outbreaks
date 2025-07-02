package com.scouter.cobblemonoutbreaks.data;

import com.mojang.serialization.Codec;
import com.scouter.cobblemonoutbreaks.registries.CORegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

public interface LevelAlgorithm {
    public static final int MAX_SPAWN_TRIES = 25;

    Codec<LevelAlgorithm> DIRECT_CODEC = CORegistries.LEVEL_ALGORITHM_TYPE_SERIALIZER.byNameCodec()
            .dispatch(LevelAlgorithm::type, LevelAlgorithmType::mapCodec);

    StreamCodec<RegistryFriendlyByteBuf, LevelAlgorithm> DIRECT_STREAM_CODEC =
            ByteBufCodecs.registry(CORegistries.LEVEL_ALGORITHM_TYPE_SERIALIZER.key())
                    .dispatch(LevelAlgorithm::type, LevelAlgorithmType::streamCodec);
    
    
    
    int getLevel(ServerLevel level, Player player);
    LevelAlgorithmType<? extends LevelAlgorithm> type();

}
