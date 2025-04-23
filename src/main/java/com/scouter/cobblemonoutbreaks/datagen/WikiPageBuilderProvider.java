package com.scouter.cobblemonoutbreaks.datagen;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static com.scouter.cobblemonoutbreaks.CobblemonOutbreaks.prefix;

public abstract class WikiPageBuilderProvider implements DataProvider {

    protected final PackOutput.PathProvider outputPath;
    private final String modid;
    private static final Logger LOGGER = LogUtils.getLogger();

    public WikiPageBuilderProvider(PackOutput output,String outputPath, String modid) {
        this.outputPath =  output.createPathProvider(PackOutput.Target.DATA_PACK, prefix(outputPath).getPath());;
        this.modid = modid;
    }

    protected abstract void generateWikiPages(BiConsumer<String, Supplier<String>> consumer);

   //protected void createWikiPage(BiConsumer<String, Supplier<String>> consumer, String fileName, String title, JsonNode jsonData) {
   //    WikiPageBuilder builder = new WikiPageBuilder(title);
   //    builder.addHeading("Introduction", 2)
   //            .addParagraph("This page describes the entity goals and how to modify them using datapacks.");

   //    if (jsonData.has("goals")) {
   //        builder.addHeading("Goals", 2);
   //        jsonData.get("goals").forEach(goal -> {
   //            String goalName = goal.get("name").asText();
   //            String description = goal.get("description").asText();
   //            builder.addList(new String[]{goalName + ": " + description});
   //        });
   //    }

   //    consumer.accept(fileName, () -> builder.getContent());
   //}

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        Map<String, Supplier<String>> entries = new HashMap<>();
        generateWikiPages(entries::put);

        return CompletableFuture.allOf(entries.entrySet().stream().map(entry -> {
            ResourceLocation fileName = prefix(entry.getKey());
            Supplier<String> contentSupplier = entry.getValue();
            Path path = outputPath.file(fileName , "md");
            return saveText(contentSupplier,path, cachedOutput);
        }).toArray(CompletableFuture[]::new));
    }


    public static CompletableFuture<?> saveText(Supplier<String> contentSupplier, Path path, CachedOutput cachedOutput) {
        return CompletableFuture.supplyAsync(contentSupplier)
                .thenApplyAsync(content -> writeToFile(content, path))
                .thenAcceptAsync(bytes -> {
                    try {
                        HashCode hashCode = Hashing.sha256().hashBytes(bytes);
                        cachedOutput.writeIfNeeded(path, bytes, hashCode);
                    } catch (IOException e) {
                        throw new UncheckedIOException("Failed to save file to " + path, e);
                    }
                });
    }

    private static byte[] writeToFile(String content, Path path) {
        try {
            // Create parent directories if they do not exist
            Files.createDirectories(path.getParent());

            // Overwrite content to file
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                writer.write(content);
            }

            // Read bytes from file
            return Files.readAllBytes(path);

        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write content to file " + path, e);
        }
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();


    public static <T> String encodeDataToJsonString(Codec<T> codec, T data) {
        return codec.encodeStart(JsonOps.INSTANCE, data)
                .map(GSON::toJson)
                .resultOrPartial(error -> LOGGER.error("Failed to encode Codec to String: {}", error)) // Logs error if any
                .orElse("{}");
    }

    public String getName() {
        return "Wiki Page Builder";
    }
}