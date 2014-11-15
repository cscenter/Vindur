package ru.csc.vindur.storage;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.bitsetFabric.BitSetFabric;
import ru.csc.vindur.bitset.bitsetFabric.EWAHBitSetFabric;
import ru.csc.vindur.document.Value;

public class IntegerRangeStoragesTest {
	private static final int VALUES_COUNT = 1000;
	private BitSetFabric bitSetFabric;
	private List<RangeStorage> storages;
	@Before
	public void createStorage() {
		bitSetFabric = new EWAHBitSetFabric();
		storages = new ArrayList<>();
		storages.add(new StorageBucketIntegers(bitSetFabric));
		storages.add(new StorageIntegers(bitSetFabric));
	}
	@Test
	public void simpleTest() {
		for(int i = 0; i < VALUES_COUNT; i ++) {
			for(RangeStorage storage: storages) {
				storage.add(i, new Value(Integer.toString(i)));
			}
		}

		for(int i = 0; i < VALUES_COUNT; i ++) {
			for(RangeStorage storage: storages) {
				assertEquals(bitSetFabric.newInstance().set(i), storage.findSet(Integer.toString(i)));
			}
		}

		int from = VALUES_COUNT / 3;
		int to = 2 * VALUES_COUNT / 3;
		
		BitSet expected = bitSetFabric.newInstance();
		for(int i = from; i <= to; i ++) {
			expected = expected.set(i);
		}
		for(RangeStorage storage: storages) {
			assertEquals(expected, storage.findRangeSet(Integer.toString(from), Integer.toString(to)));
		}
	}
}
