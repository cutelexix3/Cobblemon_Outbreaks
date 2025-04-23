package com.scouter.cobblemonoutbreaks.config.helper.fileformats;

import com.scouter.cobblemonoutbreaks.config.OutbreakConfig;
import net.minecraft.nbt.CompoundTag;

public class FileFormats {
    public static final FileFormat<CompoundTag> JSON =
            new FileFormat<>(".json",
                    new CompoundTagJsonImportStrategy(),
                    new CompoundTagJsonExportStrategy());

    public static final FileFormat<OutbreakConfig> OUTBREAK_CONFIG_FILE_FORMAT =
            new FileFormat<>(".json",
                    new GenericJsonImportStrategy<>(OutbreakConfig.CODEC),
                    new GenericJsonExportStrategy<>(OutbreakConfig.CODEC));

    public static final FileFormat<CompoundTag> NBT =
            new FileFormat<>(".nbt",
                    new CompoundTagNbtImportStrategy(),
                    new CompoundTagNbtExportStrategy());

}
