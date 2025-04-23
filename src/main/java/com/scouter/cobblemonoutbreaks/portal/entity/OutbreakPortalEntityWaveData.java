package com.scouter.cobblemonoutbreaks.portal.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class OutbreakPortalEntityWaveData {
    public static final Codec<OutbreakPortalEntityWaveData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("wave").forGetter(OutbreakPortalEntityWaveData::getWave),
                    Codec.BOOL.fieldOf("has_spawned_one").forGetter(OutbreakPortalEntityWaveData::isHasSpawnedOne)
            ).apply(instance, OutbreakPortalEntityWaveData::new)
    );

    public static OutbreakPortalEntityWaveData DEFAULT = new OutbreakPortalEntityWaveData(0, false);

    private int wave;
    private boolean hasSpawnedOne;

    public OutbreakPortalEntityWaveData(int wave, boolean hasSpawnedOne) {

        this.wave = wave;
        this.hasSpawnedOne = hasSpawnedOne;
    }

    public void setHasSpawnedOne(boolean hasSpawnedOne) {
        this.hasSpawnedOne = hasSpawnedOne;
    }

    public void setWave(int wave) {
        this.wave = wave;
    }

    public int getWave() {
        return wave;
    }

    public void increaseWave() {
        wave++;
    }

    public boolean isHasSpawnedOne() {
        return hasSpawnedOne;
    }

    @Override
    public String toString() {
        return "OutbreakPortalEntityWaveData{" +
                ", wave=" + wave +
                ", hasSpawnedOne=" + hasSpawnedOne +
                '}';
    }
}
