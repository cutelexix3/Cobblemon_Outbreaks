package com.scouter.cobblemonoutbreaks.portal.old;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.scouter.cobblemonoutbreaks.portal.PokemonRarity;

public class OutbreakSpeciesOld {


    public static final OutbreakSpeciesOld DEFAULT = new OutbreakSpeciesOld("default",1,5,1024D, PokemonRarity.COMMON);

    public static Codec<OutbreakSpeciesOld> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    Codec.STRING.fieldOf("species").forGetter(t -> t.species),
                    Codec.INT.fieldOf("waves").forGetter(w -> w.waves),
                    Codec.intRange(1, 64).fieldOf("spawns_per_wave").forGetter(s -> s.spawnsPerWave),
                    Codec.doubleRange(1,10000000).optionalFieldOf("shiny_chance",1024D).forGetter(r -> r.shinyChance),
                    PokemonRarity.CODEC.optionalFieldOf("pokemon_rarity", PokemonRarity.COMMON).forGetter(r -> r.rarity)
            )
            .apply(inst, OutbreakSpeciesOld::new)
    );

    protected final String species;
    protected int waves;
    protected int spawnsPerWave;
    protected double shinyChance;

    protected final PokemonRarity rarity;
    public OutbreakSpeciesOld(String species, int waves, int spawnsPerWave,
                              double shinyChance, PokemonRarity rarity){
        this.species = species;
        this.waves = waves;
        this.spawnsPerWave = spawnsPerWave;
        this.shinyChance = shinyChance;
        this.rarity =rarity ;
    }

    public String getSpecies() {
        return this.species;
    }

    public int getWaves() {
        return this.waves;
    }

    public int getSpawnCount() {
        return this.spawnsPerWave;
    }


    public double getShinyChance() {
        return this.shinyChance;
    }

    public PokemonRarity getRarity() {
        return rarity;
    }


}
