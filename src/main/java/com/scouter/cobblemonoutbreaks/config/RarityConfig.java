package com.scouter.cobblemonoutbreaks.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.scouter.cobblemonoutbreaks.portal.PokemonRarity;
import net.minecraft.util.RandomSource;

import java.util.EnumMap;
import java.util.Map;

public final class RarityConfig {


    private final Map<PokemonRarity, Integer> rarityProbabilities = new EnumMap<>(PokemonRarity.class);




    private final int totalRarity;
    public RarityConfig(int commonRarity, int uncommonRarity, int rareRarity, int epicRarity, int legendaryRarity) {
        rarityProbabilities.put(PokemonRarity.COMMON, commonRarity);
        rarityProbabilities.put(PokemonRarity.UNCOMMON, uncommonRarity);
        rarityProbabilities.put(PokemonRarity.RARE, rareRarity);
        rarityProbabilities.put(PokemonRarity.EPIC, epicRarity);
        rarityProbabilities.put(PokemonRarity.LEGENDARY, legendaryRarity);
        this.totalRarity = commonRarity + uncommonRarity + rareRarity + epicRarity + legendaryRarity;
    }

    public int getCommonRarity() {
        return rarityProbabilities.get(PokemonRarity.COMMON);
    }

    public int getUncommonRarity() {
        return rarityProbabilities.get(PokemonRarity.UNCOMMON);
    }

    public int getRareRarity() {
        return rarityProbabilities.get(PokemonRarity.RARE);
    }

    public int getEpicRarity() {
        return rarityProbabilities.get(PokemonRarity.EPIC);
    }

    public int getLegendaryRarity() {
        return rarityProbabilities.get(PokemonRarity.LEGENDARY);
    }

    public int getTotalRarity() {
        return totalRarity;
    }

    public int getProbability(PokemonRarity rarity) {
        return rarityProbabilities.getOrDefault(rarity, 0);
    }

    public PokemonRarity getRandomRarity(RandomSource randomSource) {
        int randomNumber = randomSource.nextInt(getTotalRarity());
        int cumulativeProbability = 0;

        for (PokemonRarity rarity : PokemonRarity.values()) {
            cumulativeProbability += getProbability(rarity);
            if (randomNumber < cumulativeProbability) {
                return rarity;
            }
        }

        return PokemonRarity.COMMON; // Fallback
    }


    public static final Codec<RarityConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("common_outbreak_rarity")
                            .orElse(40)
                            .forGetter(RarityConfig::getCommonRarity),
                    Codec.INT.fieldOf("uncommon_outbreak_rarity")
                            .orElse(30)
                            .forGetter(RarityConfig::getUncommonRarity),
                    Codec.INT.fieldOf("rare_outbreak_rarity")
                            .orElse(20)
                            .forGetter(RarityConfig::getRareRarity),
                    Codec.INT.fieldOf("epic_outbreak_rarity")
                            .orElse(8)
                            .forGetter(RarityConfig::getEpicRarity),
                    Codec.INT.fieldOf("legendary_outbreak_rarity")
                            .orElse(2)
                            .forGetter(RarityConfig::getLegendaryRarity)
            ).apply(instance, RarityConfig::new)
    );

    public static RarityConfig defaultInstance() {
        return new RarityConfig(40, 30, 20, 8, 2);
    }
}