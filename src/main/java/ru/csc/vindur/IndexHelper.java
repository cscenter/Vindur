package ru.csc.vindur;

import org.springframework.stereotype.Service;
import ru.csc.vindur.physics.BDateTimeConverter;
import ru.csc.vindur.physics.DimensionConverter;
import ru.csc.vindur.physics.PriceConverter;

/**
 * @author: Phillip Delgyado
 * Date: 24.02.14 2:50
 */
@Service
public class IndexHelper
{

    public IndexDefinition value()
    {
        return new IndexDefinition(ValueType.STRING);
    }
    public IndexDefinition enums()
    {
        return new IndexDefinition(ValueType.ENUM);
    }

    public IndexDefinition price()
    {
        return new IndexDefinition(ValueType.NUMERIC, new PriceConverter());
    }

    public IndexDefinition dimension()
    {
        return new IndexDefinition(ValueType.NUMERIC, new DimensionConverter());
    }

    public IndexDefinition datetime()
    {
        return new IndexDefinition(ValueType.NUMERIC, new BDateTimeConverter());
    }
}
