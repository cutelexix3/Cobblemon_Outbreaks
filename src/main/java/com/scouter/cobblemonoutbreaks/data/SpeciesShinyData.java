package com.scouter.cobblemonoutbreaks.data;

import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.ShinyChanceCalculationEvent;
import com.cobblemon.mod.common.config.CobblemonConfig;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.scouter.cobblemonoutbreaks.config.OutbreakConfigManager;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public class SpeciesShinyData {

    public static final SpeciesShinyData DEFAULT = new SpeciesShinyData(ShinyChoice.CONFIG, 1024D);

    private final ShinyChoice choice;
    private final double shinyChance;


    public static Codec<SpeciesShinyData> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    ShinyChoice.CODEC.fieldOf("shiny_choice").forGetter(SpeciesShinyData::getShinyChoice),
                    Codec.DOUBLE.fieldOf("shiny_chance").forGetter(SpeciesShinyData::getChance)
            )
            .apply(inst, SpeciesShinyData::new)
    );

    public SpeciesShinyData(ShinyChoice choice, double shinyChance) {
        this.choice = choice;
        this.shinyChance = shinyChance;
    }


    public double getChance() {
        return shinyChance;
    }

    public ShinyChoice getShinyChoice() {
        return choice;
    }

    public double getShinyChance(Pokemon pokemon) {
        return switch (choice) {
            case CONFIG -> OutbreakConfigManager.getConfig().getShinyConfig().getChance();
            case EVENT -> {

                ShinyChanceCalculationEvent shinyChanceCalculationEvent = new ShinyChanceCalculationEvent((float) shinyChance,pokemon );
                CobblemonEvents.SHINY_CHANCE_CALCULATION.emit(shinyChanceCalculationEvent);
                shinyChanceCalculationEvent.calculate(null);
                yield shinyChanceCalculationEvent.getChance();

            }
            default -> shinyChance;
        };
    }


    public enum ShinyChoice implements StringRepresentable {
        CONFIG,
        CUSTOM,
        EVENT;

        public static final Codec<ShinyChoice> CODEC = StringRepresentable.fromEnum(ShinyChoice::values);

        public static ShinyChoice byName(String name) {
            return ShinyChoice.valueOf(name.toLowerCase());
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public static ShinyChoice fromId(int id) {
            return ShinyChoice.values()[id];
        }
    }
}
