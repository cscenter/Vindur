package ru.csc.vindur.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Supplier;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.ROBitSet;

/**
 * @author Andrey Kokorev
 *         Created on 08.11.2014.
 */
public class StorageBucketIntegers extends StorageBase<Integer, Integer[]>
{
	// TODO test this parameter(find out the better value)
    private static final Integer DEFAULT_BUCKET_SIZE = 100;
    private final Integer bucketSize;
    private HashMap<Integer, TreeMap<Integer, BitSet>> storage; //key hash -> bucket(key -> bitset of all smaller or eq)
    private Supplier<BitSet> bitSetSupplier;

    public StorageBucketIntegers(Supplier<BitSet> bitSetSupplier)
    {
    	super(Integer.class, Integer[].class);
        this.storage = new HashMap<>();
        this.bitSetSupplier = bitSetSupplier;
        this.bucketSize = DEFAULT_BUCKET_SIZE;
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

	@Override
	public void add(int docId, Integer value) {
		incrementDocumentsCount();
        TreeMap<Integer, BitSet> bucket = getBucket(value);
        if(bucket == null) {
            bucket = new TreeMap<>();
            storage.put(getBucketNum(value), bucket);
        }

        for(Map.Entry<Integer, BitSet> e : bucket.tailMap(value).entrySet()) {
            e.getValue().set(docId);
        }

        if(bucket.containsKey(value)) {
            return;
        }
        //otherwise we should add new record to storage
        Entry<Integer, BitSet> lowerEntry = bucket.lowerEntry(value);
        BitSet bitSet;
        if (lowerEntry == null)
        {
            bitSet = bitSetSupplier.get();
        } else
        {
            bitSet = lowerEntry.getValue().copy();
        }
        bitSet.set(docId);

        bucket.put(value, bitSet);

	}

	@Override
	public ROBitSet findSet(Integer[] request) {
        Integer lowKey = request[0];
        Integer highKey = request[1];
        if (highKey < lowKey)
        { //that's not good
            return bitSetSupplier.get();
        }

        BitSet h = floorFromBucket(highKey);
        BitSet l = lowerFromBucket(lowKey);

        Integer upperBucket = getBucketNum(highKey);
        Integer lowerBucket = getBucketNum(lowKey);
        if (upperBucket == lowerBucket)
        { // values in the same bucket
            return h.xor(l); // return intersection
        }

        // Get all from l to last record in lower bucket
        TreeMap<Integer, BitSet> lowerBuc = storage.get(lowerBucket);
        BitSet result = h;
        if (lowerBuc != null)
        {
            BitSet lowerBucketLast = lowerBuc.lastEntry().getValue();
            result = result.or(l.xor(lowerBucketLast));
        }

        //Get all in middle buckets
        for (int i = lowerBucket + 1; i < upperBucket; i++)
        {
            TreeMap<Integer, BitSet> bucket = storage.get(i);
            if (bucket == null) continue;
            BitSet c = bucket.lastEntry().getValue();
            if (c != null)
            {
                result = result.or(c);
            }
        }
        return result;

	}

    public int getComplexity()
    {
        return 10;
    }

	@Override
	public boolean checkValue(Integer value, Integer[] request) {
		return value.compareTo(request[0]) >= 0 && value.compareTo(request[1]) <= 0;
	}
	
	@Override
	public boolean validateRequestType(Object request) {
		if(!super.validateRequestType(request)) {
			return false;
		}
		return ((Integer[]) request).length == 2;
	}
}
