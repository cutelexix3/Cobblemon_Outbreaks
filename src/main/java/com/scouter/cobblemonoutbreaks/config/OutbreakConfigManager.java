package com.scouter.cobblemonoutbreaks.config;

import com.mojang.logging.LogUtils;
import com.scouter.cobblemonoutbreaks.config.helper.fileformats.FileFormat;
import com.scouter.cobblemonoutbreaks.config.helper.fileformats.FileFormats;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OutbreakConfigManager {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String CONFIG_KEY = "outbreaks_config";
    private static OutbreakConfig config;

    /**
     * Public method to retrieve the loaded GeneralConfig.
     * If the config hasn't been loaded yet, it will be loaded first.
     */
    public static OutbreakConfig getConfig() {
        if (config == null) {
            try {
                loadOutbreakConfig();
            } catch (IOException e) {
                LOGGER.error("Error loading config; falling back to defaults", e);
                config = OutbreakConfig.DEFAULT;
            }
        }
        return config;
    }

    /**
     * Loads (or reloads) the config from the file.
     */


    public static void loadOutbreakConfig() throws IOException {
        config = loadConfig(FileFormats.OUTBREAK_CONFIG_FILE_FORMAT, FabricLoader.getInstance().getConfigDir(), "outbreaks_config", OutbreakConfig.DEFAULT);
    }

    public static <T> T loadConfig(FileFormat<T> format, Path configDir, String configName, T defaultValue) throws IOException {
        // Using our helper to construct the file path:
        Path configFile = getConfigFilePath(configDir, "cobblemonoutbreaks", configName, format);
        configFile = ensureFileExtension(configFile, format);
        if (!Files.exists(configFile)) {
            T config = defaultValue;
            Files.createDirectories(configFile.getParent());
            format.getExportStrategy().export(config, configFile);
            LOGGER.info("Default config created at {}", configFile);
            return defaultValue;
        } else {
            T loadedConfig = format.getImportStrategy().importData(configFile);
            if (loadedConfig != null) {
                LOGGER.info("Config successfully loaded from {}", configFile);
                return loadedConfig;
            } else {
                LOGGER.error("Failed to load config from {}. Falling back to default.", configFile);
                return defaultValue;
            }
        }
    }

    public static Path getConfigFilePath(Path configDir, String subDir, String fileName, FileFormat<?> format) {
        // Build a path like: configDir/subDir/fileName + extension
        Path subDirPath = configDir.resolve(subDir);
        String fullName = fileName.endsWith(format.getExtension())
                ? fileName
                : fileName + format.getExtension();
        return subDirPath.resolve(fullName);
    }



    /**
     * Ensures that the provided file path ends with the proper file extension.
     */
    public static <T> Path ensureFileExtension(Path filePath, FileFormat<T> format) {
        String extension = format.getExtension();
        if (!filePath.toString().endsWith(extension)) {
            return Paths.get(filePath.toString() + extension);
        }
        return filePath;
    }
    public static void loadAndReloadConfig() {
        ServerLifecycleEvents.SERVER_STARTING.register(minecraftServer ->  {
            try {
                loadOutbreakConfig();
            } catch (IOException e) {
                LOGGER.error("Failed to load config during common setup", e);
            }
        });

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, succes) -> {
            try {
                loadOutbreakConfig();
            } catch (IOException e) {
                LOGGER.error("Failed to load config during reload", e);
            }
        });
    }

}
