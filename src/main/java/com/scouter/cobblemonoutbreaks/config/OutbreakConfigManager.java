package com.scouter.cobblemonoutbreaks.config;

import com.mojang.logging.LogUtils;
import com.scouter.cobblemonoutbreaks.CobblemonOutbreaks;
import com.scouter.cobblemonoutbreaks.config.helper.fileformats.FileFormat;
import com.scouter.cobblemonoutbreaks.config.helper.fileformats.FileFormats;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
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
        config = loadConfig(FileFormats.OUTBREAK_CONFIG_FILE_FORMAT, FMLPaths.CONFIGDIR.get(), "outbreaks_config", OutbreakConfig.DEFAULT);
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
    @EventBusSubscriber(modid = CobblemonOutbreaks.MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class commonSetupConfigLoad{
        @SubscribeEvent
        public static void onCommonSetup(FMLCommonSetupEvent event) {
            try {
                loadOutbreakConfig();
            } catch (IOException e) {
                LOGGER.error("Failed to load config during common setup", e);
            }
        }

    }

    @EventBusSubscriber(modid = CobblemonOutbreaks.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static class onDataPackSynConfigLoad{

        @SubscribeEvent
        public static void onDatapackSync(OnDatapackSyncEvent event) throws IOException {
            if (event.getPlayer() == null) { // Server reload, not client sync
                loadOutbreakConfig();
            }
        }
    }

}
