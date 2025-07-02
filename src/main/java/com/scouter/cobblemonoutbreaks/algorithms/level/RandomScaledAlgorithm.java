package com.scouter.cobblemonoutbreaks.algorithms.level;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.serialization.MapCodec;
import com.scouter.cobblemonoutbreaks.data.LevelAlgorithm;
import com.scouter.cobblemonoutbreaks.data.LevelAlgorithmType;
import com.scouter.cobblemonoutbreaks.registries.LevelAlgorithmRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class RandomScaledAlgorithm implements LevelAlgorithm {

    public static final RandomScaledAlgorithm ALGO = new RandomScaledAlgorithm();

    public static final MapCodec<RandomScaledAlgorithm> CODEC = MapCodec.unit(ALGO);


    public static final StreamCodec<RegistryFriendlyByteBuf, RandomScaledAlgorithm> STREAM_CODEC = StreamCodec.unit(
            ALGO
    );
    public static final LevelAlgorithmType<RandomScaledAlgorithm> TYPE = new LevelAlgorithmType<RandomScaledAlgorithm>() {
        @Override
        public MapCodec<RandomScaledAlgorithm> mapCodec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, RandomScaledAlgorithm> streamCodec() {
            return STREAM_CODEC;
        }
    };
    public static int random(ServerLevel level, Player player) {
        return level.random.nextInt(1,100);
    }

    @Override
    public int getLevel(ServerLevel level, Player player) {
        if(player == null) return random(level, player);
        PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty((ServerPlayer) player);

        int totalLevel = 0;
        int count = 0;
        for (Pokemon pokemon : party) {
            totalLevel += pokemon.getLevel();
            count++;
        }

        if(count == 0 || totalLevel == 0){
            return random(level, player);
        }
        int scaledLevel = totalLevel/count;
        scaledLevel = level.random.nextInt(scaledLevel);

        if(scaledLevel <= 0){
            scaledLevel = 5;
        }
        if(scaledLevel > 100){
            scaledLevel = 100;
        }
        return scaledLevel;
    }

    @Override
    public LevelAlgorithmType<? extends LevelAlgorithm> type() {
        return LevelAlgorithmRegistry.RANDOM_SCALED;
    }
}
