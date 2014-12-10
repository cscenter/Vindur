package ru.csc.vindur.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Supplier;

import ru.csc.vindur.bitset.BitArray;
import ru.csc.vindur.bitset.ROBitArray;

/**
 * @author Andrey Kokorev Created on 08.11.2014.
 */
public class StorageBucketIntegers extends StorageRangeBase<Integer> {
    // TODO test this parameter(find out the better value)
    private static final Integer DEFAULT_BUCKET_SIZE = 1000;
    private final Integer bucketSize;
    private HashMap<Integer, TreeMap<Integer, BitArray>> storage;
    // key hash -> bucket(key ->/ bitset of all smaller or eq)

    public StorageBucketIntegers()
    {
        this(DEFAULT_BUCKET_SIZE);
    }

    public StorageBucketIntegers(Integer bucketSize)
    {
        super(Integer.class);
        this.storage = new HashMap<>();
        this.bucketSize = DEFAULT_BUCKET_SIZE;
    }

    private BitArray floorFromBucket(Integer key) {
        TreeMap<Integer, BitArray> bucket = getBucket(key);
        if (bucket == null)
            return BitArray.create();

        Map.Entry<Integer, BitArray> upperEntry = bucket.floorEntry(key);
        if (upperEntry == null) { // high is lower than lowest stored value, or
                                  // storage is empty
            return BitArray.create();
        }
        return upperEntry.getValue();
    }

    private BitArray lowerFromBucket(Integer key) {
        TreeMap<Integer, BitArray> bucket = getBucket(key);
        if (bucket == null)
            return BitArray.create();

        Map.Entry<Integer, BitArray> lowerEntry = bucket.lowerEntry(key);
        if (lowerEntry == null) { // high is lower than lowest stored value, or
                                  // storage is empty
            return BitArray.create();
        }
        return lowerEntry.getValue();
    }

    private Integer getBucketNum(Integer key) {
        return key / bucketSize;
    }

    private TreeMap<Integer, BitArray> getBucket(Integer key) {
        return storage.get(getBucketNum(key));
    }

    @Override
    public void add(int docId, Integer value) {
        incrementDocumentsCount();
        TreeMap<Integer, BitArray> bucket = getBucket(value);
        if (bucket == null) {
            bucket = new TreeMap<>();
            storage.put(getBucketNum(value), bucket);
        }

        for (Map.Entry<Integer, BitArray> e : bucket.tailMap(value).entrySet()) {
            e.getValue().set(docId);
        }

        if (bucket.containsKey(value)) {
            return;
        }
        // otherwise we should add new record to storage
        Entry<Integer, BitArray> lowerEntry = bucket.lowerEntry(value);
        BitArray bitSet;
        if (lowerEntry == null)
            bitSet = BitArray.create();
        else
            bitSet = lowerEntry.getValue().copy();

        bitSet.set(docId);

        bucket.put(value, bitSet);

    }

    @Override
    public ROBitArray findSet(RangeRequest request) {
        Integer lowKey = (Integer) request.getLowBound();
        Integer highKey = (Integer) request.getUpperBound();
        if (highKey < lowKey)
        { // that's not good
            return BitArray.create();
        }

        BitArray h = floorFromBucket(highKey);
        BitArray l = lowerFromBucket(lowKey);

        Integer upperBucket = getBucketNum(highKey);
        Integer lowerBucket = getBucketNum(lowKey);
        if (upperBucket == lowerBucket) { // values in the same bucket
            return h.xor(l); // return intersection
        }

        // Get all from l to last record in lower bucket
        TreeMap<Integer, BitArray> lowerBuc = storage.get(lowerBucket);
        BitArray result = h;
        if (lowerBuc != null) {
            BitArray lowerBucketLast = lowerBuc.lastEntry().getValue();
            result = result.or(l.xor(lowerBucketLast));
        }

        // Get all in middle buckets
        for (int i = lowerBucket + 1; i < upperBucket; i++) {
            TreeMap<Integer, BitArray> bucket = storage.get(i);
            if (bucket == null)
                continue;
            BitArray c = bucket.lastEntry().getValue();
            if (c != null) {
                result = result.or(c);
            }
        }
        return result;

    }

    public int getComplexity() {
        return 10;
    }
}
