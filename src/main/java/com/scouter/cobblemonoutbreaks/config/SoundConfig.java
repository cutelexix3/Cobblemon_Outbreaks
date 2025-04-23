package com.scouter.cobblemonoutbreaks.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.HashMap;
import java.util.Map;

public final class SoundConfig {
    private final Map<String, Float> volumes;
    private final boolean outbreakPortalSpawnSound;

    public SoundConfig(Map<String, Float> volumes, boolean outbreakPortalSpawnSound) {
        this.volumes = volumes;
        this.outbreakPortalSpawnSound = outbreakPortalSpawnSound;
    }

    public Map<String, Float> getVolumes() { return volumes; }
    public boolean isOutbreakPortalSpawnSound() { return outbreakPortalSpawnSound; }

    public static final Codec<SoundConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.unboundedMap(Codec.STRING, Codec.FLOAT)
                            .fieldOf("volumes")
                            .orElseGet(() -> {
                                Map<String, Float> defaults = new HashMap<>();
                                defaults.put("outbreak_portal_spawn_volume", 1.0f);
                                defaults.put("outbreak_portal_pokemon_spawn_volume", 0.2f);
                                defaults.put("outbreak_portal_shiny_pokemon_spawn_volume", 1.5f);
                                return defaults;
                            })
                            .forGetter(SoundConfig::getVolumes),
                    Codec.BOOL.fieldOf("outbreak_portal_spawn_sound").orElse(true).forGetter(SoundConfig::isOutbreakPortalSpawnSound)
            ).apply(instance, SoundConfig::new)
    );

    public static SoundConfig defaultInstance() {
        Map<String, Float> defaults = new HashMap<>();
        defaults.put("outbreak_portal_spawn_volume", 1.0f);
        defaults.put("outbreak_portal_pokemon_spawn_volume", 0.2f);
        defaults.put("outbreak_portal_shiny_pokemon_spawn_volume", 1.5f);
        return new SoundConfig(defaults, true);
    }
}