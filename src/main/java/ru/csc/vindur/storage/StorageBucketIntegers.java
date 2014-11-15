package ru.csc.vindur.storage;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.bitsetFabric.BitSetFabric;
import ru.csc.vindur.document.Value;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author Andrey Kokorev
 *         Created on 08.11.2014.
 */
//TODO тут уже сложная логика, хочу unit-testы )
public class StorageBucketIntegers implements RangeStorage {
    private static final Integer DEFAULT_BUCKET_SIZE = 100;
    private Integer bucketSize = 5000;
    private HashMap<Integer, TreeMap<Integer, BitSet>> storage; //key hash -> bucket(key -> bitset of all smaller or eq)
    private BitSetFabric bitSetFabric;
    private int size = 0;

    public StorageBucketIntegers(BitSetFabric bitSetFabric) {
        this.storage = new HashMap<>();
        this.bitSetFabric = bitSetFabric;
        this.bucketSize = DEFAULT_BUCKET_SIZE;
    }

    @Override
    public synchronized long size() {
        return size;
    }

    @Override
    public synchronized void add(int docId, Value value) {
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
        	bitSet = bitSetFabric.newInstance();
        } else {
        	bitSet = lowerEntry.getValue().clone();
        }
        bitSet.set(docId);

        bucket.put(newKey, bitSet);
    }

    @Override
    public BitSet findSet(String strictMatch) {
        Integer key = Integer.parseInt(strictMatch);

        TreeMap<Integer, BitSet> bucket = getBucket(key);
        if(bucket == null) return bitSetFabric.newInstance();

        BitSet exact = bucket.get(key);
        if(exact == null) return bitSetFabric.newInstance();

        if(bucket.firstKey().equals(key)) return exact.clone();
        BitSet low = bucket.lowerEntry(key).getValue();
        return exact.xor(low);  // everything including this or lower, except lower
    }

    @Override
    public BitSet findRangeSet(String low, String high) {
        Integer lowKey = Integer.parseInt(low);
        Integer highKey = Integer.parseInt(high);

        if(highKey > lowKey) { //that's not good
            return bitSetFabric.newInstance();
        }

        BitSet h = lowerOrEqualFromBucket(highKey);
        BitSet l = higherOrEqualFromBucket(lowKey);

        Integer upperBucket = getBucketNum(highKey);
        Integer lowerBucket = getBucketNum(lowKey);
        if(upperBucket == lowerBucket) { // values in the same bucket
            return h.xor(l); // return intersection
        }

        BitSet result = h.or(l);
        //Everything in middle buckets
        for(int i = lowerBucket + 1; i < upperBucket; i++) {
            TreeMap<Integer, BitSet> bucket = storage.get(i);
            if(bucket == null) continue;
            BitSet c = bucket.lastEntry().getValue();
            if(c != null)
                result = result.or(c);
        }
        return result;
    }

    private BitSet lowerOrEqualFromBucket(Integer key) {
        TreeMap<Integer, BitSet> bucket = getBucket(key);
        if(bucket == null) return bitSetFabric.newInstance();

        Map.Entry<Integer, BitSet> upperEntry = bucket.floorEntry(key);
        if(upperEntry == null) { //high is lower than lowest stored value, or storage is empty
            return bitSetFabric.newInstance();
        }
        return upperEntry.getValue();
    }

    private BitSet higherOrEqualFromBucket(Integer key) {
        TreeMap<Integer, BitSet> bucket = getBucket(key);
        if(bucket == null) return bitSetFabric.newInstance();

        Map.Entry<Integer, BitSet> lowerEntry = bucket.ceilingEntry(key);
        if(lowerEntry == null) { //high is lower than lowest stored value, or storage is empty
            return bitSetFabric.newInstance();
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
