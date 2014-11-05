package ru.csc.vindur.storage;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.bitsetFabric.BitSetFabric;
import ru.csc.vindur.document.Value;

import java.util.*;

import javax.annotation.concurrent.ThreadSafe;


/**
 * @author Andrey Kokorev
 *         Created on 19.10.2014.
 * For each attribute value stored a BitSet with every docId
 * with lower or equal attribute value
 */
@ThreadSafe
public final class StorageIntegers implements Storage {
    private TreeMap<Integer, BitSet> storage; //key -> bitset of all smaller
    private BitSetFabric bitSetFabric;

    public StorageIntegers(BitSetFabric bitSetFabric) {
        this.storage = new TreeMap<>();
        this.bitSetFabric = bitSetFabric;
    }

    @Override
    public synchronized long size() {
        return storage.size();
    }

    @Override
    public synchronized long expectAmount(String value) {
        return storage.size() / 1000;
    }

    @Override
    public synchronized void add(int docId, Value value) {
        Integer newKey = Integer.parseInt(value.getValue());

        boolean keyFound = false;
        for(Map.Entry<Integer, BitSet> e : storage.tailMap(newKey).entrySet()) {
            if(e.getKey().equals(newKey)) keyFound = true;
            e.getValue().set(docId);
        }

        if(keyFound) {
            return;
        }
        //otherwise we should add new record to storage
        BitSet bitSet = bitSetFabric.newInstance();
        bitSet.set(docId);

        storage.put(newKey, bitSet);
    }

    @Override
    public synchronized BitSet findSet(String strictMatch) {
        Integer key = Integer.parseInt(strictMatch);

        BitSet exact = storage.get(key);
        if(exact == null) return bitSetFabric.newInstance();

        if(storage.firstKey().equals(key)) return exact.clone();
        BitSet low = storage.lowerEntry(key).getValue();
        return exact.xor(low);  // everything including this or lower, except lower
    }
}

