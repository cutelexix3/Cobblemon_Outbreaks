package com.scouter.cobblemonoutbreaks.sound;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class DefaultOutbreakSounds {
    public static final OutbreakSounds PORTAL_SPAWN_SOUND =
        new OutbreakSounds(OutbreakSounds.SoundChoice.CONFIG, SoundEvents.PORTAL_TRIGGER, SoundSource.AMBIENT, "outbreak_portal_spawn_volume",1, 1);
        
    public static final OutbreakSounds POKEMON_SPAWN_SOUND =
        new OutbreakSounds(OutbreakSounds.SoundChoice.CONFIG, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.AMBIENT, "outbreak_portal_pokemon_spawn_volume" ,1.5F, 1);
        
    public static final OutbreakSounds POKEMON_SHINY_SOUND =
        new OutbreakSounds(OutbreakSounds.SoundChoice.CONFIG, SoundEvents.PORTAL_TRAVEL, SoundSource.HOSTILE, "outbreak_portal_shiny_pokemon_spawn_volume" ,1, 1);
}
