package com.scouter.cobblemonoutbreaks.data;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.serialization.Codec;
import com.scouter.cobblemonoutbreaks.portal.entity.OutbreakPortalEntity;
import com.scouter.cobblemonoutbreaks.registries.CORegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public interface SpawnAlgorithm {
    public static final int MAX_SPAWN_TRIES = 25;

    Codec<SpawnAlgorithm> DIRECT_CODEC = CORegistries.SPAWN_ALGORITHM_TYPE_SERIALIZER.byNameCodec()
            .dispatch(SpawnAlgorithm::type, SpawnAlgorithmType::mapCodec);

    StreamCodec<RegistryFriendlyByteBuf, SpawnAlgorithm> DIRECT_STREAM_CODEC =
            ByteBufCodecs.registry(CORegistries.SPAWN_ALGORITHM_TYPE_SERIALIZER.key())
                    .dispatch(SpawnAlgorithm::type, SpawnAlgorithmType::streamCodec);
    
    
    
    Vec3 spawnPosition(ServerLevel level, Vec3 pos, OutbreakPortalEntity entity, PokemonEntity pokemon);
    SpawnAlgorithmType<? extends SpawnAlgorithm> type();

}
