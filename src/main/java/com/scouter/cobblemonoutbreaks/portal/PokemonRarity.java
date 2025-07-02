package com.scouter.cobblemonoutbreaks.portal;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum PokemonRarity implements StringRepresentable {

    COMMON("common"),
    UNCOMMON("uncommon"),
    RARE("rare"),
    EPIC("epic"),
    LEGENDARY("legendary");
    private final String name;
    public static final Codec<PokemonRarity> CODEC = StringRepresentable.fromEnum(PokemonRarity::values);

    PokemonRarity(String name) {
        this.name = name;

    }


    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public static PokemonRarity fromId(int id) {
        return PokemonRarity.values()[id];
    }
}
