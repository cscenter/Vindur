package ru.csc.vindur.storage;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Supplier;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.ROBitSet;

/**
 * @author Andrey Kokorev Created on 19.10.2014. For each attribute value stored
 *         a BitSet with every docId with lower or equal attribute value
 */
public final class StorageRange<T extends Comparable<T>> extends
        StorageRangeBase<T> {
    private TreeMap<T, BitSet> storage; // key -> bitset of all smaller
    private Supplier<BitSet> bitSetSupplier;

    public StorageRange(Supplier<BitSet> bitSetSupplier, Class<T> type) {
        super(type);
        this.storage = new TreeMap<>();
        this.bitSetSupplier = bitSetSupplier;
    }

    @Override
    public void add(int docId, T value) {
        incrementDocumentsCount();
        for (Map.Entry<T, BitSet> e : storage.tailMap(value).entrySet()) {
            e.getValue().set(docId);
        }

        if (storage.containsKey(value)) {
            return;
        }
        // otherwise we should add new record to storage
        Entry<T, BitSet> lowerEntry = storage.lowerEntry(value);
        BitSet bitSet;
        if (lowerEntry == null) {
            bitSet = bitSetSupplier.get();
        } else {
            bitSet = lowerEntry.getValue().copy();
        }
        bitSet.set(docId);

        storage.put(value, bitSet);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ROBitSet findSet(RangeRequest request) {
        T lowKey = (T) request.getLowBound();
        T highKey = (T) request.getUpperBound();

        if (highKey.compareTo(lowKey) < 0) { // that's not good
            return bitSetSupplier.get();
        }

        Map.Entry<T, BitSet> upperEntry = storage.floorEntry(highKey);
        if (upperEntry == null) { // highKey is lower than lowest stored value,
                                  // or storage is empty
            return bitSetSupplier.get();
        }

        Map.Entry<T, BitSet> lowerEntry = storage.lowerEntry(lowKey);
        if (lowerEntry == null) { // lowKey is lower than lowest stored value
            return upperEntry.getValue().asROBitSet();
        }

        // everything is ok
        return upperEntry.getValue().xor(lowerEntry.getValue());
    }

    @Override
    public int getComplexity() {
        return 20;
    }
}