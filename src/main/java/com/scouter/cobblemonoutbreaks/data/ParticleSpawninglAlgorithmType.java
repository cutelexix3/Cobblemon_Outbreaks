package com.scouter.cobblemonoutbreaks.data;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface ParticleSpawninglAlgorithmType<T extends ParticleSpawningAlgorithm> {
    MapCodec<T> mapCodec();

    StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();
}
