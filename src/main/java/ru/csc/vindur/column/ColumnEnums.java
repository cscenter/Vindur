package ru.csc.vindur.column;

import java.util.*;

import ru.csc.vindur.bitset.BitSetUtils;
import ru.csc.vindur.document.Value;

/**
 * @author: Phillip Delgyado Date: 30.10.13 17:40
 */
public final class ColumnEnums implements Column {
	private final Map<String, BitSet> values = new HashMap<>(); // strValue->set{itemId}
	private int currentSize = 0;

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
			docsBitSet = new BitSet();
			values.put(strValue, docsBitSet);
		}
		docsBitSet.set(docId);
		currentSize++;
	}

	@Override
	public Collection<Integer> getAll() {
		BitSet resultSet = new BitSet();
		for (BitSet docsBitSet : values.values()) {
			resultSet.or(docsBitSet);
		}
		return BitSetUtils.bitSetToArrayList(resultSet);
	}

	@Override
	public Collection<Integer> findList(String value) {
		BitSet resultSet = findSet(value);
		return BitSetUtils.bitSetToArrayList(resultSet);
	}

	@Override
	public BitSet findSet(String value) {
		BitSet resultSet = values.get(value);
		if (resultSet == null) {
			return BitSetUtils.EMPTY_BITSET;
		}
		return BitSetUtils.copyOf(resultSet);
	}

}
