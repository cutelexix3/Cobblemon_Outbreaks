package com.scouter.cobblemonoutbreaks.config.helper.fileformats;

import java.io.IOException;
import java.nio.file.Path;

public interface ExportFormatStrategy<T> {
    void export(T data, Path filePath) throws IOException;
    String getExtension();
}
