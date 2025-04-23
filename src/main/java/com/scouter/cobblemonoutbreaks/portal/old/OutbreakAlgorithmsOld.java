package com.scouter.cobblemonoutbreaks.portal.old;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import static com.scouter.cobblemonoutbreaks.CobblemonOutbreaks.prefix;

public class OutbreakAlgorithmsOld {

    public static Codec<OutbreakAlgorithmsOld> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    ResourceLocation.CODEC.optionalFieldOf("spawn_algorithm", prefix("clustered")).forGetter(g -> g.spawnAlgorithms),
                    ResourceLocation.CODEC.optionalFieldOf("level_algorithm", prefix("scaled")).forGetter(g -> g.spawnLevelAlgorithms),
                    Codec.intRange(1, 99).optionalFieldOf("min_pokemon_level", 100).forGetter(s -> s.minPokemonLevel),
                    Codec.intRange(2, 100).optionalFieldOf("max_pokemon_level", 100).forGetter(s -> s.maxPokemonLevel),
                    Codec.doubleRange(5D,40D).optionalFieldOf("spawn_range", 15D).forGetter(r -> r.spawnRange),
                    Codec.doubleRange(5D,40D).optionalFieldOf("leash_range", 32D).forGetter(g -> g.leashRange)
            )
            .apply(inst, OutbreakAlgorithmsOld::new)
    );


    private final ResourceLocation spawnLevelAlgorithms;
    private final ResourceLocation spawnAlgorithms;
    protected int maxPokemonLevel;
    protected int minPokemonLevel;
    protected double spawnRange;
    protected double leashRange;


    public OutbreakAlgorithmsOld(ResourceLocation spawnAlgorithms, ResourceLocation spawnLevelAlgorithms, int minPokemonLevel, int maxPokemonLevel, double spawnRange, double leashRange){
        this.spawnLevelAlgorithms = spawnLevelAlgorithms;
        this.spawnAlgorithms = spawnAlgorithms;
        this.minPokemonLevel = minPokemonLevel;
        this.maxPokemonLevel = maxPokemonLevel;
        this.spawnRange = spawnRange;
        this.leashRange = leashRange;
    }

    public ResourceLocation getSpawnAlgo() {
        return spawnAlgorithms;
    }

    public ResourceLocation getSpawnLevelAlgo() {
        return spawnLevelAlgorithms;
    }

    public int getMaxPokemonLevel() {
        return this.maxPokemonLevel;
    }

    public int getMinPokemonLevel() {
        return this.minPokemonLevel;
    }

    public double getSpawnRange() {
        return this.spawnRange;
    }

    public double getLeashRangeSq() {
        return this.leashRange * this.leashRange;
    }

    public static OutbreakAlgorithmsOld getDefaultAlgoritms(){
        return new OutbreakAlgorithmsOld(prefix("clustered"), prefix("scaled"),10,100, 15,32);
    }

}
