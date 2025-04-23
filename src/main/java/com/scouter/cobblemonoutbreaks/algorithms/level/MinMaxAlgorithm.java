package com.scouter.cobblemonoutbreaks.algorithms.level;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.scouter.cobblemonoutbreaks.data.LevelAlgorithm;
import com.scouter.cobblemonoutbreaks.data.LevelAlgorithmType;
import com.scouter.cobblemonoutbreaks.registries.LevelAlgorithmRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

public class MinMaxAlgorithm implements LevelAlgorithm {


    public static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<MinMaxAlgorithm> CODEC = RecordCodecBuilder.<MinMaxAlgorithm>mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("min_pokemon_level").forGetter(MinMaxAlgorithm::getMinLevel),
            Codec.INT.fieldOf("max_pokemon_level").forGetter(MinMaxAlgorithm::getMaxLevel)
    ).apply(instance, MinMaxAlgorithm::new)).validate(e -> {
        return (e.minLevel > e.maxLevel || e.minLevel == e.maxLevel) ? DataResult.error(() -> {
            return "min_pokemon_level needs to be smaller than max_pokemon_level";
        }) : DataResult.success(e);    });

    public static final StreamCodec<RegistryFriendlyByteBuf, MinMaxAlgorithm> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, MinMaxAlgorithm::getMinLevel,
            ByteBufCodecs.VAR_INT, MinMaxAlgorithm::getMaxLevel,
            MinMaxAlgorithm::new
    );

    public final int minLevel;
    public final int maxLevel;

    public MinMaxAlgorithm(int minLevel, int maxLevel) {
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public static final LevelAlgorithmType<MinMaxAlgorithm> TYPE = new LevelAlgorithmType<MinMaxAlgorithm>() {
        @Override
        public MapCodec<MinMaxAlgorithm> mapCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MinMaxAlgorithm> streamCodec() {
            return STREAM_CODEC;
        }
    };
    
    @Override
    public int getLevel(ServerLevel level, Player player
    ) {
        int maxPokemonLevel = maxLevel;
        int minPokemonLevel = minLevel;




        return level.random.nextInt(minPokemonLevel, maxPokemonLevel);
    }

    @Override
    public LevelAlgorithmType<? extends LevelAlgorithm> type() {
        return LevelAlgorithmRegistry.MIN_MAX.get();
    }
}
