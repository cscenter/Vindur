package ru.csc.njord;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author: Phillip Delgyado
 * Date: 06.02.14 3:05
 */
@Service
public class EngineConfig
{
    private Map<String,IndexDefinition> indexes;
    private Integer expectedVolume;

    public EngineConfig(Map<String, IndexDefinition> indexes, Integer expectedVolume)
    {
        this.indexes = indexes;
        this.expectedVolume = expectedVolume;
    }

    public Map<String, IndexDefinition> getIndexes()
    {
        return indexes;
    }

    public Integer getExpectedVolume()
    {
        return expectedVolume;
    }



    public static class IndexDefinition
    {
        private StorageType type;
        private IndexNumerics.NumberConverter numberConverter;

        public IndexDefinition(StorageType type)
        {
            this.type = type;
        }

        public IndexDefinition(StorageType type, IndexNumerics.NumberConverter numberConverter)
        {
            this.type = type;
            this.numberConverter = numberConverter;
        }

        public StorageType getType()
        {
            return type;
        }

        public IndexNumerics.NumberConverter getNumberConverter()
        {
            return numberConverter;
        }

        public static EngineConfig.IndexDefinition DefIndexDefinition()
        {
            return new EngineConfig.IndexDefinition(StorageType.values);
        }

    }

}
