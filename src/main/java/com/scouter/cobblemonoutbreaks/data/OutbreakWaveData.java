package com.scouter.cobblemonoutbreaks.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class OutbreakWaveData {


    public static final OutbreakWaveData DEFAULT = new OutbreakWaveData(1,5);

    public static Codec<OutbreakWaveData> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    Codec.INT.fieldOf("waves").forGetter(OutbreakWaveData::getWaves),
                    Codec.INT.fieldOf("spawns_per_wave").forGetter(OutbreakWaveData::getSpawnsPerWave)
            )
            .apply(inst, OutbreakWaveData::new)
    );

    private final int waves;
    private final int spawnsPerWave;

    public OutbreakWaveData(int waves, int spawnsPerWave) {
        this.waves = waves;
        this.spawnsPerWave = spawnsPerWave;
    }

    public int getSpawnsPerWave() {
        return spawnsPerWave;
    }

    public int getWaves() {
        return waves;
    }
}
