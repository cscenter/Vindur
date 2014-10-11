package ru.csc.vindur;

import java.util.Map;

/**
 * Created by Pavel Chursin on 05.10.2014.
 */
public class EngineConfig {
    Map<String, IndexDefinition> indexes;
    int expectedVolume;

    String fileName;

    public EngineConfig(Map<String, IndexDefinition> indexes, int expectedVolume,
                        String filename) {
        this.indexes = indexes;
        this.expectedVolume = expectedVolume;
        this.fileName = filename;
    }

    public Map<String, IndexDefinition> getIndexes() {
        return indexes;
    }

    public int getExpectedVolume() {
        return expectedVolume;
    }

    public String getFileName() {
        return fileName;
    }

}
