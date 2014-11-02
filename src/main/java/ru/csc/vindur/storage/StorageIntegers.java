package ru.csc.vindur.storage;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.bitsetFabric.BitSetFabric;
import ru.csc.vindur.document.Value;

import java.util.*;


/**
 * @author Andrey Kokorev
 *         Created on 19.10.2014.
 * For each attribute value stored a BitSet with every docId
 * with lower or equal attribute value
 */
public final class StorageIntegers implements Storage {
    private TreeMap<Integer, BitSet> storage; //key -> bitset of all smaller
    private BitSetFabric bitSetFabric;

    public StorageIntegers(BitSetFabric bitSetFabric) {
        this.storage = new TreeMap<>();
        this.bitSetFabric = bitSetFabric;
    }

    @Override
    public long size() {
        return storage.size();
    }

    @Override
    public long expectAmount(String value) {
        return storage.size() / 1000;
    }

    @Override
    public void add(int docId, Value value) {
        Integer newKey = Integer.parseInt(value.getValue());

        for(BitSet bitSet : storage.tailMap(newKey).values()) {
            bitSet.set(docId);
        }

        if(storage.containsKey(newKey)) {
            return;
        }
        //otherwise we should add new record to storage
        BitSet bitSet = bitSetFabric.newInstance();
        bitSet.set(docId);

        storage.put(newKey, bitSet);
    }

    @Override
    public BitSet findSet(String strictMatch) {
        Integer key = Integer.parseInt(strictMatch);

        BitSet exact = storage.get(key);
        if(exact == null) return bitSetFabric.newInstance();

        if(storage.firstKey().equals(key)) return bitSetFabric.newInstance(exact);
        BitSet low = storage.lowerEntry(key).getValue();
        return exact.xor(low);  // everything including this or lower, except lower
    }
}

