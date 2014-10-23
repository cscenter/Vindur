package ru.csc.vindur.column;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.bitsetFabric.BitSetFabric;
import ru.csc.vindur.document.Value;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author: Phillip Delgyado Date: 30.10.13 17:40
 */
public final class ColumnNumerics implements Column {
	private final List<Record> values = new ArrayList<>();
	private final BitSetFabric bitSetFabric;
	private boolean isSorted = false;
	private int currentSize = 0;

	public ColumnNumerics(BitSetFabric bitSetFabric) {
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
	public final void add(int docId, Value value) {
		Record newRecord = new Record();
		newRecord.itemId = docId;
		newRecord.value = new BigDecimal(value.toString());
		values.add(newRecord);
		isSorted = false;
		currentSize++;
	}

	@Override
	public Collection<Integer> getAll() {
		return Lists.transform(values, new Function<Record, Integer>() {
			@Nullable
			@Override
			public Integer apply(@Nullable Record input) {
				return input.itemId;
			}
		});
	}

	@Override
	public Collection<Integer> findList(String strValue) {
		BigDecimal value = new BigDecimal(strValue);

		if (isSorted == false) {
			Collections.sort(values, comarator);
			isSorted = true;
		}

		Record key = new Record();
		key.value = value;
		int someIdx = Collections.binarySearch(values, key, comarator);
		
		// this is a bottleneck
		int leftBorderId = leftBorder(values, value, someIdx);
		int rightBorderId = rightBorder(values, value, someIdx);

		Collection<Integer> resultCollection = new ArrayList<>(rightBorderId - leftBorderId);
		for (int index = leftBorderId; index < rightBorderId; index++) {
			resultCollection.add(values.get(index).itemId);
		}

		return resultCollection;
	}

	@Override
	public BitSet findSet(String value) {
		return bitSetFabric.newInstance(findList(value));
	}

	public static int leftBorder(List<Record> data, BigDecimal value, int idx) {
		int ii = idx;
		if (idx < 0) {
			return -1;
		}

		while (ii > 0) {
			Record rt = data.get(ii);
			if (!recordValueEquals(rt, value))
				break;
			ii--;
		}
		return ii;
	}

	public static int rightBorder(List<Record> data, BigDecimal value, int idx) {
		int ii = idx + 1;
		if (idx < 0) {
			return -1;
		}

		int len = data.size();
		while (ii < len) {
			Record rt = data.get(ii);
			if (!recordValueEquals(rt, value))
				break;
			ii++;
		}
		return ii;
	}

	public static boolean recordValueEquals(Record r, BigDecimal value) {
		return (r.value.equals(value));
	}

	private final Comparator<Record> comarator = new Comparator<Record>() {
		public int compare(Record o1, Record o2) {
			return o1.value.compareTo(o2.value);
		}
	};

	private static final class Record {
		BigDecimal value;
		int itemId;

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			Record record = (Record) o;

			if (itemId != record.itemId)
				return false;
			if (value != null ? !value.equals(record.value)
					: record.value != null)
				return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = value != null ? value.hashCode() : 0;
			result = 31 * result + (int) (itemId ^ (itemId >>> 32));
			return result;
		}
	}
}
