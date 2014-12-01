package ru.csc.vindur.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.csc.vindur.storage.StorageType;
import ru.csc.vindur.test.utils.AttributeGenerator;
import ru.csc.vindur.test.utils.RandomUtils;

import com.google.common.collect.Lists;

/**
 * Created by jetbrains on 07.11.2014.
 */
public class SimpleTestBuilder implements TestBuilder {
    private static final Logger LOG = LoggerFactory
            .getLogger(SimpleTestBuilder.class);

    private int attributesCount;
    private Map<StorageType, Double> typeFrequencies = new HashMap<>();
    private Map<StorageType, Integer> valuesCount = new HashMap<>();

    private Map<String, StorageType> types;
    private Map<String, Object[]> values;

    public static SimpleTestBuilder build(int attributesCount) {
        SimpleTestBuilder t = new SimpleTestBuilder();
        t.attributesCount = attributesCount;
        t.types = new HashMap<>();
        t.values = new HashMap<>();
        return t;
    }

    public SimpleTestBuilder setValuesCount(StorageType type, Integer value) {
        valuesCount.put(type, value);
        return this;
    }

    public SimpleTestBuilder setTypeFrequence(StorageType type, Double value) {
        typeFrequencies.put(type, value);
        return this;
    }

    public SimpleTestBuilder init() {
        LOG.debug(" init storage for {} attributes", attributesCount);
        for (int i = 0; i < attributesCount; i++) {
            String storageName = RandomUtils.getString(1, 10);
            StorageType storageType = RandomUtils.getFrec(typeFrequencies);
            types.put(storageName, storageType);
            values.put(
                    storageName,
                    AttributeGenerator.generateValues(storageType,
                            valuesCount.get(storageType)));
        }

        for (String key : types.keySet()) {
            LOG.debug("storage '{}'/{} with values {} ", key, types.get(key)
                    .name(), values.get(key));
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
    public Double getProbability(String key) {
        Random random = new Random();
        return random.nextDouble();
    }

}
