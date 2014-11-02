package ru.csc.vindur;

import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import javax.annotation.concurrent.ThreadSafe;

import ru.csc.vindur.bitset.bitsetFabric.BitSetFabric;
import ru.csc.vindur.document.StorageType;

/**
 * Created by Pavel Chursin on 05.10.2014.
 */
@ThreadSafe
public class EngineConfig {
    private final ConcurrentMap<String, StorageType> indexes;  // attribute -> value type
    private final BitSetFabric bitSetFabric;
	private final ExecutorService executorService;

    public EngineConfig(Map<String, StorageType> indexes, BitSetFabric bitSetFabric, ExecutorService executorService) {
    	synchronized (indexes) {
            this.indexes = new ConcurrentHashMap<>(indexes);
		}
    	this.executorService = executorService;
        this.bitSetFabric = bitSetFabric;
    }
    
    public StorageType getValueType(String attribute) {
        return indexes.get(attribute);
    }

	public BitSetFabric getBitSetFabric() {
		return bitSetFabric;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}
}
