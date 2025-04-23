package com.scouter.cobblemonoutbreaks.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public final class GeneralConfig {
    private final boolean outbreakSpawnRewards;
    private final int outbreaksFlushTimer;
    private final int tempOutbreaksFlushTimer;
    private final boolean outbreakParticles;

    public GeneralConfig(boolean outbreakSpawnRewards, boolean outbreakParticles, int outbreaksFlushTimer, int tempOutbreaksFlushTimer) {
        this.outbreakSpawnRewards = outbreakSpawnRewards;
        this.outbreaksFlushTimer = outbreaksFlushTimer;
        this.tempOutbreaksFlushTimer = tempOutbreaksFlushTimer;
        this.outbreakParticles = outbreakParticles;
    }

    public boolean isOutbreakParticles() {
        return outbreakParticles;
    }

    public boolean isOutbreakSpawnRewards() { return outbreakSpawnRewards; }
    public int getOutbreaksFlushTimer() { return outbreaksFlushTimer; }
    public int getTempOutbreaksFlushTimer() { return tempOutbreaksFlushTimer; }

    public static final Codec<GeneralConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("outbreak_spawn_rewards").orElse(true).forGetter(GeneralConfig::isOutbreakSpawnRewards),
                    Codec.BOOL.fieldOf("spawn_portal_particles").orElse(false).forGetter(GeneralConfig::isOutbreakParticles),
                    Codec.INT.fieldOf("outbreaks_flush_timer").orElse(432000).forGetter(GeneralConfig::getOutbreaksFlushTimer),
                    Codec.INT.fieldOf("temp_outbreaks_flush_timer").orElse(72000).forGetter(GeneralConfig::getTempOutbreaksFlushTimer)
            ).apply(instance, GeneralConfig::new)
    );

    public static GeneralConfig defaultInstance() {
        return new GeneralConfig(true, true,432000, 72000);
    }
}