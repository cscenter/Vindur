package ru.csc.vindur.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import ru.csc.vindur.bitset.BitArray;
import ru.csc.vindur.bitset.ROBitArray;

/**
 * @author: Phillip Delgyado Date: 30.10.13 17:40
 */
public final class StorageExact<T> extends Storage<T, T> {
    private final Map<T, BitArray> values = new HashMap<>(); // strValue->{itemIds}

    public StorageExact(Class<T> type)
    {
        super(type, type);
    }

    @Override
    public synchronized void add(int docId, T value) {
        BitArray valsSet = values.get(value);
        if (valsSet == null) {
            valsSet = BitArray.create();
            values.put(value, valsSet);
        }
        valsSet.set(docId);
        incrementDocumentsCount();
    }

    @Override
    public ROBitArray findSet(T request) {
        BitArray valsSet = values.get(request);
        if (valsSet == null) {
            return BitArray.create();
        }
        return valsSet;
    }

    @Override
    public boolean checkValue(int docId, T value, T request) {
        return value.equals(request);
    }

    @Override
    public int getComplexity() {
        return 10;
    }
}
