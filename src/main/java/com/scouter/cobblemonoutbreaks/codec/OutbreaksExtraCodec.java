package com.scouter.cobblemonoutbreaks.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.ChunkPos;

public class OutbreaksExtraCodec {
    public static final Codec<Boolean> FLEXIBLE_BOOL_CODEC = new Codec<Boolean>() {
        @Override
        public <T> DataResult<T> encode(Boolean input, DynamicOps<T> ops, T prefix) {
            // Always encode using the standard boolean codec (true/false)
            return Codec.BOOL.encode(input, ops, prefix);
        }

        @Override
        public <T> DataResult<Pair<Boolean, T>> decode(DynamicOps<T> ops, T input) {
            // First, try decoding as a boolean.
            DataResult<Pair<Boolean, T>> boolResult = Codec.BOOL.decode(ops, input);
            if (boolResult.result().isPresent()) {
                return boolResult;
            } else {
                // If that fails, try decoding as an integer and interpret nonzero as true.
                return Codec.INT.decode(ops, input).map(pair -> Pair.of(pair.getFirst() != 0, pair.getSecond()));
            }
        }

        @Override
        public String toString() {
            return "FLEXIBLE_BOOL_CODEC";
        }
    };

    public static final Codec<ChunkPos> CHUNK_POS_CODEC = RecordCodecBuilder.create((p_122642_) -> {
        return p_122642_.group(Codec.LONG.fieldOf("chunk_long").forGetter(ChunkPos::toLong)).apply(p_122642_, ChunkPos::new);
    });
    public static final Codec<ChunkPos> CHUNK_POS_CODEC_STRING  = Codec.STRING.comapFlatMap(str -> parseChunkPos(str), intVal ->  Long.toString(intVal.toLong()));

    private static final DataResult<ChunkPos> parseChunkPos(String instantString) {
        try {
            return DataResult.success(new ChunkPos(Long.parseLong(instantString)));
        } catch (NumberFormatException e) {
            return DataResult.error(() -> e.getMessage());
        }
    }
}

