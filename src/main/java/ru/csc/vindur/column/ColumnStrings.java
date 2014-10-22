package ru.csc.vindur.column;

import com.google.common.collect.Sets;

import java.util.*;

import ru.csc.vindur.document.Value;

/**
 * @author: Phillip Delgyado Date: 30.10.13 17:40
 */
public final class ColumnStrings implements Column {
	private Map<String, TreeSet<Integer>> values; // strValue->{itemId}
	private int currentSize;
	private int maxSize;

	public ColumnStrings(int maxsize) {
		this.maxSize = maxsize;
		values = new HashMap<>();
		currentSize = 0;
	}

	@Override
	public long size() {
		return currentSize;
	}

	@Override
	public long expectAmount(String value) {
		return currentSize / 100 + 1;
	}

	@Override
	public void add(int docId, Value value) {
		String strValue = value.getValue();
		TreeSet<Integer> vals = values.get(strValue);
		if (vals == null) {
			vals = new TreeSet<>();
			values.put(strValue, vals);
		}
		vals.add(docId);
		currentSize++;
	}

	@Override
	public Collection<Integer> getAll() {
		Collection<Integer> resultCollection = new ArrayList<>(currentSize);
		for (TreeSet<Integer> docsSet : values.values()) {
			resultCollection.addAll(docsSet);
		}
		
		return resultCollection;
	}

	@Override
	public Collection<Integer> findList(String value) {
		if (!values.containsKey(value)) {
			return Collections.emptyList();
		}

		Collection<Integer> resultCollection = new ArrayList<>(values.get(value));
		return resultCollection;
	}

	@Override
	public BitSet findSet(String value) {
		BitSet resultBitSet = new BitSet(maxSize);
		for (int docId : findList(value)) {
			resultBitSet.set(docId);
		}
		return resultBitSet;
	}

}
