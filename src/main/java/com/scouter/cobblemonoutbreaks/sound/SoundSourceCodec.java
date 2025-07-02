package com.scouter.cobblemonoutbreaks.sound;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.sounds.SoundSource;

public class SoundSourceCodec {
    public static final Codec<SoundSource> CODEC = Codec.STRING.comapFlatMap(s -> {
        for (SoundSource source : SoundSource.values()) {
            if (source.getName().equals(s)) {
                return DataResult.success(source);
            }
        }
        return DataResult.error(() -> "Unknown SoundSource: " + s);
    }, SoundSource::getName);
}