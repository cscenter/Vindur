package ru.csc.vindur;

import java.util.Map;
import java.util.Set;

import ru.csc.vindur.document.ValueType;

/**
 * Created by Pavel Chursin on 05.10.2014.
 */
public class EngineConfig {
    private Map<String, ValueType> indexes;  // attribute -> value type
    private int expectedVolume;

    public EngineConfig(Map<String, ValueType> indexes, int expectedVolume) {
        this.indexes = indexes;
        this.expectedVolume = expectedVolume;
    }

    public Set<String> getAttributes() {
        return indexes.keySet();
    }

    public ValueType getValueType(String attribute) {
        return indexes.get(attribute);
    }

    public int getExpectedVolume() {
        return expectedVolume;
    }
}
