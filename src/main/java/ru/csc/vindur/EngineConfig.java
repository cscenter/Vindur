package ru.csc.vindur;

import java.util.Map;
import java.util.Set;

import ru.csc.vindur.document.ValueType;

/**
 * Created by Pavel Chursin on 05.10.2014.
 */
public class EngineConfig {
    private final Map<String, ValueType> indexes;  // attribute -> value type

    public EngineConfig(Map<String, ValueType> indexes) {
        this.indexes = indexes;
    }

    public Set<String> getAttributes() {
        return indexes.keySet();
    }

    public ValueType getValueType(String attribute) {
        return indexes.get(attribute);
    }
}
