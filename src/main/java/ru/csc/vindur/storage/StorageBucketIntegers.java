package ru.csc.vindur.storage;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.ROBitSet;
import ru.csc.vindur.document.Value;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;

/**
 * @author Andrey Kokorev
 *         Created on 08.11.2014.
 */
public class StorageBucketIntegers implements RangeStorage, ExactStorage
{
    private static final Integer DEFAULT_BUCKET_SIZE = 100;
    private final Integer bucketSize;
    private HashMap<Integer, TreeMap<Integer, BitSet>> storage; //key hash -> bucket(key -> bitset of all smaller or eq)
    private Supplier<BitSet> bitSetSupplier;
    private int size = 0;

    public StorageBucketIntegers(Supplier<BitSet> bitSetSupplier)
    {
        this.storage = new HashMap<>();
        this.bitSetSupplier=bitSetSupplier;
        this.bucketSize = DEFAULT_BUCKET_SIZE;
    }

    @Override
    public synchronized long size() {
        return size;
    }

    @Override
    public synchronized void add(int docId, Value value) {
    	// TODO what if parse method throws NumberFormatException?
        Integer newKey = Integer.parseInt(value.getValue());
        size++;

        TreeMap<Integer, BitSet> bucket = getBucket(newKey);
        if(bucket == null) {
            bucket = new TreeMap<>();
            storage.put(getBucketNum(newKey), bucket);
        }

        for(Map.Entry<Integer, BitSet> e : bucket.tailMap(newKey).entrySet()) {
            e.getValue().set(docId);
        }

        if(bucket.containsKey(newKey)) {
            return;
        }
        //otherwise we should add new record to storage
        Entry<Integer, BitSet> lowerEntry = bucket.lowerEntry(newKey);
        BitSet bitSet;
        if(lowerEntry == null) {
        	bitSet = bitSetSupplier.get();
        } else {
        	bitSet = lowerEntry.getValue().copy();
        }
        bitSet.set(docId);

        bucket.put(newKey, bitSet);
    }

    @Override
    public ROBitSet findSet(String strictMatch) {
        Integer key = Integer.parseInt(strictMatch);

        TreeMap<Integer, BitSet> bucket = getBucket(key);
        if(bucket == null) return bitSetSupplier.get();

        BitSet exact = bucket.get(key);
        if(exact == null) return bitSetSupplier.get();

        if(bucket.firstKey().equals(key)) return exact.asROBitSet();
        BitSet low = bucket.lowerEntry(key).getValue();
        return exact.xor(low);  // everything including this or lower, except lower
    }

    @Override
    public ROBitSet findRangeSet(String low, String high) {
        Integer lowKey = Integer.parseInt(low);
        Integer highKey = Integer.parseInt(high);

        if(highKey < lowKey) { //that's not good
            return bitSetSupplier.get();
        }

        BitSet h = floorFromBucket(highKey);
        BitSet l = lowerFromBucket(lowKey);

        Integer upperBucket = getBucketNum(highKey);
        Integer lowerBucket = getBucketNum(lowKey);
        if(upperBucket == lowerBucket) { // values in the same bucket
            return h.xor(l); // return intersection
        }

        // Get all from l to last record in lower bucket
        TreeMap<Integer, BitSet> lowerBuc = storage.get(lowerBucket);
        BitSet result = h;
        if(lowerBuc != null) {
	        BitSet lowerBucketLast = lowerBuc.lastEntry().getValue();
	        result = result.or(l.xor(lowerBucketLast));
        }
        
        //Get all in middle buckets
        for(int i = lowerBucket + 1; i < upperBucket; i++) {
            TreeMap<Integer, BitSet> bucket = storage.get(i);
            if(bucket == null) continue;
            BitSet c = bucket.lastEntry().getValue();
            if(c != null)
                result = result.or(c);
        }
        return result;
    }

    private BitSet floorFromBucket(Integer key) {
        TreeMap<Integer, BitSet> bucket = getBucket(key);
        if(bucket == null) return bitSetSupplier.get();

        Map.Entry<Integer, BitSet> upperEntry = bucket.floorEntry(key);
        if(upperEntry == null) { //high is lower than lowest stored value, or storage is empty
            return bitSetSupplier.get();
        }
        return upperEntry.getValue();
    }

    private BitSet lowerFromBucket(Integer key) {
        TreeMap<Integer, BitSet> bucket = getBucket(key);
        if(bucket == null) return bitSetSupplier.get();

        Map.Entry<Integer, BitSet> lowerEntry = bucket.lowerEntry(key);
        if(lowerEntry == null) { //high is lower than lowest stored value, or storage is empty
            return bitSetSupplier.get();
        }
        return lowerEntry.getValue();
    }

    private Integer getBucketNum(Integer key) {
        return key / bucketSize;
    }

    private TreeMap<Integer, BitSet> getBucket(Integer key) {
        return storage.get(getBucketNum(key));
    }
}
