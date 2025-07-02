package com.scouter.cobblemonoutbreaks.config.helper.fileformats;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import org.slf4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class GenericJsonExportStrategy<T> implements ExportFormatStrategy<T>{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Codec<T> codec;

    public GenericJsonExportStrategy(Codec<T> codec) {
        this.codec = codec;
    }

    @Override
    public void export(T data, Path filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            codec.encodeStart(JsonOps.INSTANCE, data)
                    .ifError(jsonElementError -> LOGGER.error("Failed to export data {} to file path {}",data, filePath))
                    .ifSuccess(element -> GSON.toJson(element, writer));
        }
    }

    @Override
    public String getExtension() {
        return ".json";
    }
}
