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
public final class StorageIntegers implements RangeStorage {
    private TreeMap<Integer, BitSet> storage; //key -> bitset of all smaller
    private BitSetFabric bitSetFabric;

    public StorageIntegers(BitSetFabric bitSetFabric) {
        this.storage = new TreeMap<>();
        this.bitSetFabric = bitSetFabric;
    }

    @Override
    public  long size() {
        return storage.size();
    }

    @Override
    public void add(int docId, Value value) {
        Integer newKey = Integer.parseInt(value.getValue());

        for(Map.Entry<Integer, BitSet> e : storage.tailMap(newKey).entrySet()) {
            e.getValue().set(docId);
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

        if(storage.firstKey().equals(key)) return exact.clone();
        BitSet low = storage.lowerEntry(key).getValue();
        return exact.xor(low);  // everything including this or lower, except lower
    }

    @Override
    public BitSet findRangeSet(String low, String high) {
        Integer lowKey = Integer.parseInt(low);
        Integer highKey = Integer.parseInt(high);

        if(highKey > lowKey) { //that's not good
            return bitSetFabric.newInstance();
        }

        Map.Entry<Integer, BitSet> upperEntry = storage.floorEntry(highKey);
        if(upperEntry == null) { //highKey is lower than lowest stored value, or storage is empty
            return bitSetFabric.newInstance();
        }

        Map.Entry<Integer, BitSet> lowerEntry = storage.lowerEntry(lowKey);
        if(lowerEntry == null) { //lowKey is lower than lowest stored value
            return upperEntry.getValue().clone();
        }

        //everything is alright
        return upperEntry.getValue().xor(lowerEntry.getValue());
    }
}

