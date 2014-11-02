package ru.csc.vindur;

import java.util.Map;
import java.util.Set;

import ru.csc.vindur.bitset.bitsetFabric.BitSetFabric;
import ru.csc.vindur.document.StorageType;

/**
 * Created by Pavel Chursin on 05.10.2014.
 */
public class EngineConfig {
    private final Map<String, StorageType> indexes;  // attribute -> value type
    private final BitSetFabric bitSetFabric;

    public EngineConfig(Map<String, StorageType> indexes, BitSetFabric bitSetFabric) {
        this.indexes = indexes;
        this.bitSetFabric = bitSetFabric;
    }

    public Set<String> getAttributes() {
        return indexes.keySet();
    }

    public StorageType getValueType(String attribute) {
        return indexes.get(attribute);
    }

	public BitSetFabric getBitSetFabric() {
		return bitSetFabric;
	}
}
