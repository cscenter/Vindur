package ru.csc.vindur.column;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.bitsetFabric.BitSetFabric;
import ru.csc.vindur.document.Value;

/**
 * @author: Phillip Delgyado Date: 30.10.13 17:40
 */
public final class StorageStrings implements Storage {
	private final Map<String, TreeSet<Integer>> values = new HashMap<>(); // strValue->{itemId}
	private final BitSetFabric bitSetFabric;
	private int currentSize = 0;

	public StorageStrings(BitSetFabric bitSetFabric) {
		this.bitSetFabric = bitSetFabric;
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
	public Collection<Integer> findList(String value) {
		if (!values.containsKey(value)) {
			return Collections.emptyList();
		}

		return Collections.unmodifiableCollection(values.get(value));
	}

    //TODO: неэффективная реализация. По идее, если нет данных - надо сразу отдавать BetSet.EMPTY.
    //И с ним эффективно работать в оптимизаторе. Создавать новый - не слишком осмысленно )
	@Override
	public BitSet findSet(String value) {
		return bitSetFabric.newInstance(findList(value));
	}

}
