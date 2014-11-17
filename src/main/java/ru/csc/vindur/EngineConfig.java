package ru.csc.vindur;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

import javax.annotation.concurrent.ThreadSafe;

import ru.csc.vindur.bitset.bitsetFabric.BitSetFabric;
import ru.csc.vindur.document.StorageType;
import ru.csc.vindur.optimizer.Optimizer;
import ru.csc.vindur.optimizer.TinyOptimizer;

/**
 * Created by Pavel Chursin on 05.10.2014.
 */

public class EngineConfig
{
    private final Map<String, StorageType> indexes;  // attribute -> value type
    private final BitSetFabric bitSetFabric;
    private final Optimizer optimizer;

    public EngineConfig(Map<String, StorageType> indexes, BitSetFabric bitSetFabric, Optimizer optimizer) {
        this.indexes = indexes;
        this.bitSetFabric = bitSetFabric;
        this.optimizer = optimizer;
    }

    public StorageType getValueType(String attribute) {
        return indexes.get(attribute);
    }

	public BitSetFabric getBitSetFabric() {
		return bitSetFabric;
	}

    public Optimizer getOptimizer() { return optimizer; }
}
