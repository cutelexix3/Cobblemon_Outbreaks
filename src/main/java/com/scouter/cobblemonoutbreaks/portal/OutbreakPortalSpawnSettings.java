package com.scouter.cobblemonoutbreaks.portal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class OutbreakPortalSpawnSettings {

    public static final OutbreakPortalSpawnSettings DEFAULT = new OutbreakPortalSpawnSettings(
            -63,
            253,
            OutbreakPortalSpawnBiomes.DEFAULT
    );
    public static Codec<OutbreakPortalSpawnSettings> CODEC = RecordCodecBuilder.<OutbreakPortalSpawnSettings>create(inst -> inst
            .group(
                    Codec.intRange(-63, 255).fieldOf("outbreak_min_y").forGetter(OutbreakPortalSpawnSettings::getMinOutbreakY),
                    Codec.intRange(-63, 255).fieldOf("outbreak_max_y").forGetter(OutbreakPortalSpawnSettings::getMaxOutbreakY),
                    OutbreakPortalSpawnBiomes.CODEC.fieldOf("biome_settings").forGetter(OutbreakPortalSpawnSettings::getOutbreakPortalSpawnBiomes)
            )
            .apply(inst, OutbreakPortalSpawnSettings::new)).validate(
            (data) -> {
                return data.minOutbreakY > data.maxOutbreakY ? DataResult.error(() -> {
                    return "outbreak_min_y needs to be smaller or equal to outbreak_max_y";
                }) : DataResult.success(data);
            });

    private final int minOutbreakY;
    private final int maxOutbreakY;
    private final OutbreakPortalSpawnBiomes outbreakPortalSpawnBiomes;

    public OutbreakPortalSpawnSettings(int minOutbreakY, int maxOutbreakY, OutbreakPortalSpawnBiomes outbreakPortalSpawnBiomes) {
        this.minOutbreakY = minOutbreakY;
        this.maxOutbreakY = maxOutbreakY;
        this.outbreakPortalSpawnBiomes = outbreakPortalSpawnBiomes;
    }

    public int getMaxOutbreakY() {
        return maxOutbreakY;
    }

    public int getMinOutbreakY() {
        return minOutbreakY;
    }

    public OutbreakPortalSpawnBiomes getOutbreakPortalSpawnBiomes() {
        return outbreakPortalSpawnBiomes;
    }
}
