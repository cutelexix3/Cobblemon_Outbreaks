package com.scouter.cobblemonoutbreaks.config.helper.fileformats;

public final class FileFormat<T> {
    private final String extension;
    private final ImportFormatStrategy<T> importStrategy;
    private final ExportFormatStrategy<T> exportStrategy;

    public FileFormat(String extension, ImportFormatStrategy<T> importStrategy, ExportFormatStrategy<T> exportStrategy) {
        this.extension = extension;
        this.importStrategy = importStrategy;
        this.exportStrategy = exportStrategy;
    }

    public String getExtension() {
        return extension;
    }

    public ImportFormatStrategy<T> getImportStrategy() {
        return importStrategy;
    }

    public ExportFormatStrategy<T> getExportStrategy() {
        return exportStrategy;
    }
}
