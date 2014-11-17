package ru.csc.vindur.storage;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.ROBitSet;
import ru.csc.vindur.bitset.bitsetFabric.BitSetFabric;
import ru.csc.vindur.document.Value;

import java.util.*;
import java.util.Map.Entry;


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
    	// TODO what if parse method throws NumberFormatException?
        Integer newKey = Integer.parseInt(value.getValue());

        for(Map.Entry<Integer, BitSet> e : storage.tailMap(newKey).entrySet()) {
            e.getValue().set(docId);
        }

        if(storage.containsKey(newKey)) {
            return;
        }
        //otherwise we should add new record to storage
        Entry<Integer, BitSet> lowerEntry = storage.lowerEntry(newKey);
        BitSet bitSet;
        if(lowerEntry == null) {
        	bitSet = bitSetFabric.newInstance();
        } else {
        	bitSet = lowerEntry.getValue().copy();
        }
        bitSet.set(docId);

        storage.put(newKey, bitSet);
    }

    @Override
    public ROBitSet findSet(String strictMatch) {
        Integer key = Integer.parseInt(strictMatch);

        BitSet exact = storage.get(key);
        if(exact == null) return bitSetFabric.newInstance();

        if(storage.firstKey().equals(key)) return exact.asROBitSet();
        BitSet low = storage.lowerEntry(key).getValue();
        return exact.xor(low);  // everything including this or lower, except lower
    }

    @Override
    public ROBitSet findRangeSet(String low, String high) {
        Integer lowKey = Integer.parseInt(low);
        Integer highKey = Integer.parseInt(high);

        if(highKey < lowKey) { //that's not good
            return bitSetFabric.newInstance();
        }

        Map.Entry<Integer, BitSet> upperEntry = storage.floorEntry(highKey);
        if(upperEntry == null) { //highKey is lower than lowest stored value, or storage is empty
            return bitSetFabric.newInstance();
        }

        Map.Entry<Integer, BitSet> lowerEntry = storage.lowerEntry(lowKey);
        if(lowerEntry == null) { //lowKey is lower than lowest stored value
            return upperEntry.getValue().asROBitSet();
        }

        //everything is alright
        return upperEntry.getValue().xor(lowerEntry.getValue());
    }
}

