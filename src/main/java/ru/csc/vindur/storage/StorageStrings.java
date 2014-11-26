package ru.csc.vindur.storage;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import javax.annotation.concurrent.ThreadSafe;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.ROBitSet;

/**
 * @author: Phillip Delgyado Date: 30.10.13 17:40
 */
@ThreadSafe
public final class StorageStrings implements Storage<String, String>
{
    private final Map<String, BitSet> values = new HashMap<>(); // strValue->{itemIds}
    private final Supplier<BitSet> bitSetSupplier;
    private AtomicInteger currentSize = new AtomicInteger();

    public StorageStrings(Supplier<BitSet> bitSetSupplier)
    {
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
        currentSize.incrementAndGet();
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

	@Override
	public int documentsCount() {
		return currentSize.get();
	}

	@Override
	public boolean validateValueType(Object value) {
		return value instanceof String;
	}

	@Override
	public boolean validateRequestType(Object value) {
		return value instanceof String;
	}

}
