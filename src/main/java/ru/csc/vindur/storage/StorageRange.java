package ru.csc.vindur.storage;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Supplier;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.ROBitSet;


/**
 * @author Andrey Kokorev
 *         Created on 19.10.2014.
 *         For each attribute value stored a BitSet with every docId
 *         with lower or equal attribute value
 */
public final class StorageRange<T extends Comparable<T>> extends StorageBase<T, T[]>
{
    private TreeMap<T, BitSet> storage; //key -> bitset of all smaller
    private Supplier<BitSet> bitSetSupplier;

    @SuppressWarnings("unchecked")
	public StorageRange(Supplier<BitSet> bitSetSupplier, Class<T> type)
    {
    	super(type, (Class<T[]>) java.lang.reflect.Array.newInstance(type, 0).getClass());
        this.storage = new TreeMap<>();
        this.bitSetSupplier = bitSetSupplier;
    }

    @Override
    public void add(int docId, T value)
    {
    	incrementDocumentsCount();
        for (Map.Entry<T, BitSet> e : storage.tailMap(value).entrySet())
        {
            e.getValue().set(docId);
        }

        if (storage.containsKey(value))
        {
            return;
        }
        //otherwise we should add new record to storage
        Entry<T, BitSet> lowerEntry = storage.lowerEntry(value);
        BitSet bitSet;
        if (lowerEntry == null)
        {
            bitSet = bitSetSupplier.get();
        } else
        {
            bitSet = lowerEntry.getValue().copy();
        }
        bitSet.set(docId);

        storage.put(value, bitSet);
    }
    @Override
    public ROBitSet findSet(T[] request)
    {
        T lowKey = request[0];
        T highKey = request[1];

        if (highKey.compareTo(lowKey) < 0)
        { //that's not good
            return bitSetSupplier.get();
        }

        Map.Entry<T, BitSet> upperEntry = storage.floorEntry(highKey);
        if (upperEntry == null)
        { //highKey is lower than lowest stored value, or storage is empty
            return bitSetSupplier.get();
        }

        Map.Entry<T, BitSet> lowerEntry = storage.lowerEntry(lowKey);
        if (lowerEntry == null)
        { //lowKey is lower than lowest stored value
            return upperEntry.getValue().asROBitSet();
        }

        //everything is ok
        return upperEntry.getValue().xor(lowerEntry.getValue());
    }

	@Override
	public boolean checkValue(T value, T[] request) {
		return value.compareTo(request[0]) >= 0 && value.compareTo(request[1]) <= 0;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean validateRequestType(Object request) {
		if(!super.validateRequestType(request)) {
			return false;
		}
		return ((T[]) request).length == 2;
	}
}