package dev.footer.gutils.lib;

public enum JsonExportDirs {
    BLOCK("block"),
    ITEM("item"),
    BIOME("biome")
    ;
    private final String dirName;

    JsonExportDirs(String dirName) {
        this.dirName = dirName;
    }

    public String getDirName() {
        return dirName;
    }
}
