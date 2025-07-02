package com.scouter.cobblemonoutbreaks.config.helper.fileformats;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class GenericJsonImportStrategy<T> implements ImportFormatStrategy<T> {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final Codec<T> codec;

    public GenericJsonImportStrategy(Codec<T> codec) {
        this.codec = codec;
    }

    @Override
    @Nullable
    public T importData(Path filePath) throws IOException {
        try (FileReader reader = new FileReader(filePath.toFile())) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            return codec.decode(JsonOps.INSTANCE, jsonElement)
                    .resultOrPartial(err -> LOGGER.error("Failed to load data for path {}: {}", filePath, err))
                    .map(Pair::getFirst)
                    .orElse(null);
        }
    }



    @Override
    public String getExtension() {
        return "";
    }
}
