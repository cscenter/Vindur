package ru.csc.vindur.storage;


import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.concurrent.ThreadSafe;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.ROBitSet;

/**
 * @author: Phillip Delgyado Date: 30.10.13 17:40
 */
@ThreadSafe
public final class StorageStrings extends StorageBase<String, String>
{
    private final Map<String, BitSet> values = new HashMap<>(); // strValue->{itemIds}
    private final Supplier<BitSet> bitSetSupplier;

    public StorageStrings(Supplier<BitSet> bitSetSupplier)
    {
    	super(String.class, String.class);
        this.bitSetSupplier = bitSetSupplier;
    }

    @Override
    public synchronized void add(int docId, String value)
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
    public ROBitSet findSet(String request)
    {
        BitSet valsSet = values.get(request);
        if (valsSet == null)
        {
            return bitSetSupplier.get();
        }
        return valsSet;
    }

	@Override
	public boolean checkValue(String value, String request) {
		return value.equals(request);
	}
}
