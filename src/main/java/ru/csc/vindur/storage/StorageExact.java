package ru.csc.vindur.storage;


import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.ROBitSet;

/**
 * @author: Phillip Delgyado Date: 30.10.13 17:40
 */
public final class StorageExact<T> extends StorageBase<T, T>
{
    private final Map<T, BitSet> values = new HashMap<>(); // strValue->{itemIds}
    private final Supplier<BitSet> bitSetSupplier;

    public StorageExact(Supplier<BitSet> bitSetSupplier, Class<T> type)
    {
    	super(type, type);
        this.bitSetSupplier = bitSetSupplier;
    }

    @Override
    public synchronized void add(int docId, T value)
    {
    	BitSet valsSet = values.get(value);
        if (valsSet == null)
        {
            valsSet = bitSetSupplier.get();
            values.put(value, valsSet);
        }
        valsSet.set(docId);
        incrementDocumentsCount();
    }

    @Override
    public ROBitSet findSet(T request)
    {
        BitSet valsSet = values.get(request);
        if (valsSet == null)
        {
            return bitSetSupplier.get();
        }
        return valsSet;
    }

	@Override
	public boolean checkValue(T value, T request) {
		return value.equals(request);
	}

    @Override
    public int getComplexity()
    {
        return 10;
    }
}
