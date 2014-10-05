package ru.csc.njord;

import org.springframework.stereotype.Service;
import ru.csc.njord.physics.BDateTimeConverter;
import ru.csc.njord.physics.DimensionConverter;
import ru.csc.njord.physics.PriceConverter;

/**
 * @author: Phillip Delgyado
 * Date: 24.02.14 2:50
 */
@Service
public class IndexHelper
{

    public EngineConfig.IndexDefinition values()
    {
        return new EngineConfig.IndexDefinition(StorageType.values);
    }
    public EngineConfig.IndexDefinition enums()
    {
        return new EngineConfig.IndexDefinition(StorageType.enums);
    }

    public EngineConfig.IndexDefinition price()
    {
        return new EngineConfig.IndexDefinition(StorageType.numerics, new PriceConverter());
    }

    public EngineConfig.IndexDefinition dimension()
    {
        return new EngineConfig.IndexDefinition(StorageType.numerics, new DimensionConverter());
    }

    public EngineConfig.IndexDefinition datetime()
    {
        return new EngineConfig.IndexDefinition(StorageType.numerics, new BDateTimeConverter());
    }
}
