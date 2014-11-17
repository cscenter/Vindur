package ru.csc.vindur.storage;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import javax.annotation.concurrent.ThreadSafe;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.ROBitSet;
import ru.csc.vindur.document.Value;

/**
 * @author: Phillip Delgyado Date: 30.10.13 17:40
 */
@ThreadSafe
public final class StorageStrings implements Storage
{
	private final Map<String, BitSet> values = new HashMap<>(); // strValue->{itemIds}
	private final Supplier<BitSet> bitSetSupplier;
	private AtomicInteger currentSize = new AtomicInteger();

	public StorageStrings (Supplier<BitSet> bitSetSupplier) {
		this.bitSetSupplier = bitSetSupplier;
	}

	@Override
	public long size() {
		return currentSize.longValue();
	}

	@Override
	public synchronized void add(int docId, Value value) {
		String strValue = value.getValue();
		BitSet valsSet = values.get(strValue);
		if (valsSet == null) {
			valsSet = bitSetSupplier.get();
			values.put(strValue, valsSet);
		}
		valsSet.set(docId);
		currentSize.incrementAndGet();
	}

	@Override
	public ROBitSet findSet(String strValue) {
		BitSet valsSet = values.get(strValue);
		if(valsSet == null) {
			return bitSetSupplier.get();
		}
		return valsSet;
	}

}
