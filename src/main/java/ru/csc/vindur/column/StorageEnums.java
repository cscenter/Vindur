package ru.csc.vindur.column;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.bitsetFabric.BitSetFabric;
import ru.csc.vindur.document.Value;

/**
 * @author: Phillip Delgyado Date: 30.10.13 17:40
 */
public final class StorageEnums implements Storage {
	private final Map<String, BitSet> values = new HashMap<>(); // strValue->set{itemId}
	private final BitSetFabric bitSetFabric;
	private int currentSize = 0;

	public StorageEnums(BitSetFabric bitSetFabric) {
		this.bitSetFabric = bitSetFabric;
	}

	@Override
	public long size() {
		return currentSize;
	}

	@Override
	public long expectAmount(String value) {
		return currentSize / 10000 + 1;
	}

	@Override
	public void add(int docId, Value value) {
		String strValue = value.getValue();
		BitSet docsBitSet = values.get(strValue);
		if (docsBitSet == null) {
			docsBitSet = bitSetFabric.newInstance();
			values.put(strValue, docsBitSet);
		}
		docsBitSet.set(docId);
		currentSize++;
	}

	@Override
	public Collection<Integer> findList(String value) {
		BitSet resultSet = findSet(value);
		return resultSet.toIntList();
	}

	@Override
	public BitSet findSet(String value) {
		BitSet resultSet = values.get(value);
		if (resultSet == null) {
			return bitSetFabric.newInstance();
		}
		return bitSetFabric.newInstance(resultSet);
	}

}
