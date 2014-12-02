package ru.csc.vindur;

import java.util.Map;
import java.util.function.Supplier;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.executor.Executor;
import ru.csc.vindur.storage.StorageType;

/**
 * Created by Pavel Chursin on 05.10.2014.
 */

public class EngineConfig {
    private final Map<String, StorageType> indexes; // attribute -> value type
    private final Executor executor;
    private final Supplier<BitSet> bitSetSupplier;

    public EngineConfig(Map<String, StorageType> indexes,
            Supplier<BitSet> bitSetSupplier, Executor executor) {
        this.indexes = indexes;
        this.executor = executor;
        this.bitSetSupplier = bitSetSupplier;
    }

    public StorageType getValueType(String attribute) {
        return indexes.get(attribute);
    }

    public Supplier<BitSet> getBitSetSupplier() {
        return bitSetSupplier;
    }

    public Executor getExecutor() {
        return executor;
    }
}
