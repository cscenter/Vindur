package ru.csc.vindur.test;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.csc.vindur.storage.StorageType;
import ru.csc.vindur.test.utils.AttributeGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jetbrains on 07.11.2014.
 */
public class TunableTestBuilder implements TestBuilder
{
    private static final Logger LOG = LoggerFactory
            .getLogger(TunableTestBuilder.class);

    private Map<String, Integer> valuesCount = new HashMap<>();
    private Map<String, Double> valuesProp = new HashMap<>();

    private Map<String, StorageType> types;
    private Map<String, Object[]> values;

    public static TunableTestBuilder build()
    {
        TunableTestBuilder t = new TunableTestBuilder();
        t.types = new HashMap<>();
        t.values = new HashMap<>();
        return t;
    }

   public TunableTestBuilder storage(String key, StorageType type, int valCnt, double props)
   {
       types.put(key,type);
       valuesCount.put(key,valCnt);
       valuesProp.put(key,props);
       return this;
   }

    public TunableTestBuilder init()
    {
        for (String key : types.keySet())
        {
            values.put(key,
                    AttributeGenerator.generateValues(types.get(key),
                            valuesCount.get(key)));
        }
        return this;
    }

    @Override
    public Map<String, StorageType> getTypes() {
        return types;
    }

    @Override
    public List<String> getStorages() {
        return Lists.newArrayList(types.keySet());
    }

    @Override
    public Object[] getValues(String key) {
        return values.get(key);
    }

    @Override
    public Double getProbability(String key)
    {
     return valuesProp.get(key);
    }

}
