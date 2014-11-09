package ru.csc.vindur.storage;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.ThreadSafe;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.bitsetFabric.BitSetFabric;
import ru.csc.vindur.document.Value;

/**
 * @author: Phillip Delgyado Date: 30.10.13 17:40
 */
@ThreadSafe
public final class StorageStrings implements Storage {
	private final Map<String, BitSet> values = new HashMap<>(); // strValue->{itemIds}
	private final BitSetFabric bitSetFabric;
	private AtomicInteger currentSize = new AtomicInteger();

	public StorageStrings(BitSetFabric bitSetFabric) {
		this.bitSetFabric = bitSetFabric;
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
			valsSet = bitSetFabric.newInstance();
			values.put(strValue, valsSet);
		}
		valsSet.set(docId);
		currentSize.incrementAndGet();
	}

	@Override
	public BitSet findSet(String strValue) {
		BitSet valsSet = values.get(strValue);
		if(valsSet == null) {
			return bitSetFabric.newInstance();
		}
		return valsSet;
	}

}
