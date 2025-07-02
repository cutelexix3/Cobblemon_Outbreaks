package com.scouter.cobblemonoutbreaks.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.scouter.cobblemonoutbreaks.algorithms.level.RandomAlgorithm;
import com.scouter.cobblemonoutbreaks.algorithms.particle.CustomParticleAlgorithm;
import com.scouter.cobblemonoutbreaks.algorithms.spawning.OpenFieldAlgorithm;

public class OutbreakAlgorithmsData {

    public static final OutbreakAlgorithmsData DEFAULT = new OutbreakAlgorithmsData(RandomAlgorithm.ALGO, OpenFieldAlgorithm.ALGO, CustomParticleAlgorithm.DEBUG_PARTICLE);

    public static Codec<OutbreakAlgorithmsData> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    LevelAlgorithm.DIRECT_CODEC.fieldOf("level_algorithm").forGetter(OutbreakAlgorithmsData::getLevelAlgorithm),
                    SpawnAlgorithm.DIRECT_CODEC.fieldOf("spawn_algorithm").forGetter(OutbreakAlgorithmsData::getSpawnAlgorithm),
                    ParticleSpawningAlgorithm.DIRECT_CODEC.optionalFieldOf("particle_spawn_algorithm", CustomParticleAlgorithm.DEBUG_PARTICLE).forGetter(OutbreakAlgorithmsData::getParticleSpawningAlgorithm)
            )
            .apply(inst, OutbreakAlgorithmsData::new)
    );
    private final LevelAlgorithm levelAlgorithm;
    private final SpawnAlgorithm spawnAlgorithm;
    private final ParticleSpawningAlgorithm particleSpawningAlgorithm;

    public OutbreakAlgorithmsData(LevelAlgorithm levelAlgorithm, SpawnAlgorithm spawnAlgorithm, ParticleSpawningAlgorithm particleSpawningAlgorithm) {
        this.levelAlgorithm = levelAlgorithm;
        this.spawnAlgorithm = spawnAlgorithm;
        this.particleSpawningAlgorithm = particleSpawningAlgorithm;
    }

    public LevelAlgorithm getLevelAlgorithm() {
        return levelAlgorithm;
    }

    public ParticleSpawningAlgorithm getParticleSpawningAlgorithm() {
        return particleSpawningAlgorithm;
    }

    public SpawnAlgorithm getSpawnAlgorithm() {
        return spawnAlgorithm;
    }
}
