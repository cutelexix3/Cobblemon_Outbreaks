package com.scouter.cobblemonoutbreaks.config.helper.fileformats;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public class CompoundTagNbtImportStrategy implements ImportFormatStrategy<CompoundTag> {

    @Override
    public CompoundTag importData(Path filePath) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(filePath.toFile())) {
            return NbtIo.readCompressed(inputStream, NbtAccounter.unlimitedHeap());
        }
    }

    @Override
    public String getExtension() {
        return ".nbt";
    }
}
