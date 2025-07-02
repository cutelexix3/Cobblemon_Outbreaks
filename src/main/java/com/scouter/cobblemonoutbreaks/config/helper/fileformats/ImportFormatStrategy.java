package com.scouter.cobblemonoutbreaks.config.helper.fileformats;

import java.io.IOException;
import java.nio.file.Path;

public interface ImportFormatStrategy<T> {
    T importData(Path filePath) throws IOException;
    String getExtension();
}
