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
public final class StorageIntegers implements Storage<Integer, Integer[]>
{
    private TreeMap<Integer, BitSet> storage; //key -> bitset of all smaller
    private Supplier<BitSet> bitSetSupplier;

    public StorageIntegers(Supplier<BitSet> bitSetSupplier)
    {
        this.storage = new TreeMap<>();
        this.bitSetSupplier = bitSetSupplier;
    }

    @Override
    public void add(int docId, Integer value)
    {
        for (Map.Entry<Integer, BitSet> e : storage.tailMap(value).entrySet())
        {
            e.getValue().set(docId);
        }

        if (storage.containsKey(value))
        {
            return;
        }
        //otherwise we should add new record to storage
        Entry<Integer, BitSet> lowerEntry = storage.lowerEntry(value);
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
    public ROBitSet findSet(Integer[] request)
    {
        Integer lowKey = request[0];
        Integer highKey = request[1];

        if (highKey < lowKey)
        { //that's not good
            return bitSetSupplier.get();
        }

        Map.Entry<Integer, BitSet> upperEntry = storage.floorEntry(highKey);
        if (upperEntry == null)
        { //highKey is lower than lowest stored value, or storage is empty
            return bitSetSupplier.get();
        }

        Map.Entry<Integer, BitSet> lowerEntry = storage.lowerEntry(lowKey);
        if (lowerEntry == null)
        { //lowKey is lower than lowest stored value
            return upperEntry.getValue().asROBitSet();
        }

        //everything is ok
        return upperEntry.getValue().xor(lowerEntry.getValue());
    }

	@Override
	public boolean checkValue(Integer value, Integer[] request) {
		return value.compareTo(request[0]) >= 0 && value.compareTo(request[1]) <= 0;
	}

	@Override
	public int documentsCount() {
		// TODO fix. Should be documents count. Not values count
		return storage.size();
	}

	@Override
	public boolean validateValueType(Object value) {
		return value instanceof Integer;
	}

	@Override
	public boolean validateRequestType(Object value) {
		if(!(value instanceof Integer[])) {
			return false;
		}
		return ((Integer[]) value).length == 2;
	}
}