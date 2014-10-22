package ru.csc.vindur.column;

import java.util.*;

import ru.csc.vindur.document.Value;

/**
 * @author: Phillip Delgyado Date: 30.10.13 17:40
 */
public final class ColumnEnums implements Column {
	private Map<String, BitSet> values; // strValue->set{itemId}
	private int currentSize;
	private int maxSize;

	public ColumnEnums(int maxSize) {
		this.maxSize = maxSize;
		values = new HashMap<>();
		currentSize = 0;
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
			docsBitSet = new BitSet(maxSize);
			values.put(strValue, docsBitSet);
		}
		docsBitSet.set(docId);
		currentSize++;
	}

	@Override
	public Collection<Integer> getAll() {
		BitSet resultSet = new BitSet(maxSize);
		for (BitSet docsBitSet : values.values()) {
			resultSet.or(docsBitSet);
		}
		Collection<Integer> resultCollection = new ArrayList<>(resultSet.cardinality());
		for (int docId = resultSet.nextSetBit(0); docId >= 0; docId = resultSet.nextSetBit(docId + 1)) {
			resultCollection.add(docId);
		}
		return resultCollection;
	}

	@Override
	public Collection<Integer> findList(String value) {
		BitSet resultSet = findSet(value);
		Collection<Integer> items = new ArrayList<>(resultSet.cardinality());
		for (int docId = resultSet.nextSetBit(0); docId >= 0; docId = resultSet.nextSetBit(docId + 1)) {
			items.add(docId);
		}
		return items;
	}

	@Override
	public BitSet findSet(String value) {
		BitSet resultSet = values.get(value);
		if (resultSet == null) {
			return new BitSet();
		}
		return (BitSet) resultSet.clone(); // BitSet implements Cloneable
	}

}
