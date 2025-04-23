package com.scouter.cobblemonoutbreaks.config.helper.fileformats;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

public class CompoundTagNbtExportStrategy implements ExportFormatStrategy<CompoundTag> {

    @Override
    public void export(CompoundTag data, Path filePath) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(filePath.toFile())) {
            NbtIo.writeCompressed(data, outputStream);
        }
    }

    @Override
    public String getExtension() {
        return ".nbt";
    }
}
