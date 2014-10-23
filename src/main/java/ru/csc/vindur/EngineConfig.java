package ru.csc.vindur;

import java.util.Map;
import java.util.Set;

import ru.csc.vindur.bitset.bitsetFabric.BitSetFabric;
import ru.csc.vindur.document.ValueType;

/**
 * Created by Pavel Chursin on 05.10.2014.
 */
public class EngineConfig {
    private final Map<String, ValueType> indexes;  // attribute -> value type
    private final BitSetFabric bitSetFabric;

    public EngineConfig(Map<String, ValueType> indexes, BitSetFabric bitSetFabric) {
        this.indexes = indexes;
        this.bitSetFabric = bitSetFabric;
    }

    public Set<String> getAttributes() {
        return indexes.keySet();
    }

    public ValueType getValueType(String attribute) {
        return indexes.get(attribute);
    }

	public BitSetFabric getBitSetFabric() {
		return bitSetFabric;
	}
}
