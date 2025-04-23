package com.scouter.cobblemonoutbreaks.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.scouter.cobblemonoutbreaks.data.SpeciesShinyData;

public class ShinyConfig {

    private final double shinyChance;


    public static Codec<ShinyConfig> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    Codec.DOUBLE.fieldOf("shiny_chance").forGetter(ShinyConfig::getChance)
            )
            .apply(inst, ShinyConfig::new)
    );

    public ShinyConfig(double shinyChance) {
        this.shinyChance = shinyChance;
    }


    public double getChance() {
        return shinyChance;
    }

    public static ShinyConfig defaultInstance() {
        return new ShinyConfig(1024D);
    }
}
