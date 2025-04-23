package com.scouter.cobblemonoutbreaks.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.scouter.cobblemonoutbreaks.sound.DefaultOutbreakSounds;
import com.scouter.cobblemonoutbreaks.sound.OutbreakSounds;

public class OutbreakSoundsData {

    public static final OutbreakSoundsData DEFAULT = new OutbreakSoundsData(DefaultOutbreakSounds.PORTAL_SPAWN_SOUND, DefaultOutbreakSounds.POKEMON_SPAWN_SOUND, DefaultOutbreakSounds.POKEMON_SHINY_SOUND);

    public static final Codec<OutbreakSoundsData> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    OutbreakSounds.CODEC.fieldOf("portal_spawn_sound").forGetter(OutbreakSoundsData::getPortalSpawnSound),
                    OutbreakSounds.CODEC.fieldOf("pokemon_spawn_sound").forGetter(OutbreakSoundsData::getPokemonSpawnSound),
                    OutbreakSounds.CODEC.fieldOf("pokemon_shiny_sound").forGetter(OutbreakSoundsData::getPokemonShinySound)
            )
            .apply(instance, OutbreakSoundsData::new)
    );


    private final OutbreakSounds portalSpawnSound;
    private final OutbreakSounds pokemonSpawnSound;
    private final OutbreakSounds pokemonShinySound;
    public OutbreakSoundsData(OutbreakSounds portalSpawnSound, OutbreakSounds pokemonSpawnSound, OutbreakSounds pokemonShinySound) {
        this.portalSpawnSound = portalSpawnSound;
        this.pokemonSpawnSound = pokemonSpawnSound;
        this.pokemonShinySound = pokemonShinySound;
    }

    public OutbreakSounds getPokemonSpawnSound() {
        return pokemonSpawnSound;
    }

    public OutbreakSounds getPortalSpawnSound() {
        return portalSpawnSound;
    }

    public OutbreakSounds getPokemonShinySound() {
        return pokemonShinySound;
    }
}
