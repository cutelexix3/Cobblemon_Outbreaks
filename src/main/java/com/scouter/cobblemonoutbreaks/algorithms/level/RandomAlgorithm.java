package com.scouter.cobblemonoutbreaks.algorithms.level;

import com.mojang.serialization.MapCodec;
import com.scouter.cobblemonoutbreaks.data.LevelAlgorithm;
import com.scouter.cobblemonoutbreaks.data.LevelAlgorithmType;
import com.scouter.cobblemonoutbreaks.registries.LevelAlgorithmRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

public class RandomAlgorithm implements LevelAlgorithm {

    public static final RandomAlgorithm ALGO = new RandomAlgorithm();

    public static final MapCodec<RandomAlgorithm> CODEC = MapCodec.unit(ALGO);


    public static final StreamCodec<RegistryFriendlyByteBuf, RandomAlgorithm> STREAM_CODEC = StreamCodec.unit(
            ALGO
    );
    public static final LevelAlgorithmType<RandomAlgorithm> TYPE = new LevelAlgorithmType<RandomAlgorithm>() {
        @Override
        public MapCodec<RandomAlgorithm> mapCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, RandomAlgorithm> streamCodec() {
            return STREAM_CODEC;
        }
    };
    
    
    
    @Override
    public int getLevel(ServerLevel level, Player player) {
        return level.random.nextInt(1,100);
    }

    @Override
    public LevelAlgorithmType<? extends LevelAlgorithm> type() {
        return LevelAlgorithmRegistry.RANDOM.get();
    }
}
