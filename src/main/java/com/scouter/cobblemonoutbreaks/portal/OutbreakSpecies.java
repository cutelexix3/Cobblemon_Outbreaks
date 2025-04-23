package com.scouter.cobblemonoutbreaks.portal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.scouter.cobblemonoutbreaks.data.OutbreakWaveData;
import com.scouter.cobblemonoutbreaks.data.SpeciesShinyData;

public class OutbreakSpecies {

    public static final OutbreakSpecies DEFAULT = new OutbreakSpecies
            ("default",
                    OutbreakWaveData.DEFAULT,
                    SpeciesShinyData.DEFAULT,
                    PokemonRarity.COMMON
            );

    public static Codec<OutbreakSpecies> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    Codec.STRING.fieldOf("pokemon").forGetter(OutbreakSpecies::getSpecies),
                    OutbreakWaveData.CODEC.fieldOf("wave_data").forGetter(OutbreakSpecies::getWaveData),
                    SpeciesShinyData.CODEC.fieldOf("species_shiny_data").forGetter(OutbreakSpecies::getSpeciesShinyData),
                    PokemonRarity.CODEC.fieldOf("pokemon_rarity").forGetter(OutbreakSpecies::getPokemonRarity)

            )
            .apply(inst, OutbreakSpecies::new)
    );
    private final String species;
    private final OutbreakWaveData waveData;
    private final SpeciesShinyData speciesShinyData;
    private final PokemonRarity pokemonRarity;

    public OutbreakSpecies(String species, OutbreakWaveData waveData, SpeciesShinyData speciesShinyData, PokemonRarity pokemonRarity) {
        this.species = species;
        this.waveData = waveData;
        this.speciesShinyData = speciesShinyData;
        this.pokemonRarity = pokemonRarity;
    }


    public OutbreakWaveData getWaveData() {
        return waveData;
    }

    public PokemonRarity getPokemonRarity() {
        return pokemonRarity;
    }

    public SpeciesShinyData getSpeciesShinyData() {
        return speciesShinyData;
    }

    public String getSpecies() {
        return species;
    }
}
