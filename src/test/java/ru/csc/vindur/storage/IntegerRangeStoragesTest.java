package ru.csc.vindur.storage;

import org.junit.Before;
import org.junit.Test;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.EWAHBitSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

public class IntegerRangeStoragesTest {
	private static final int VALUES_COUNT = 1000;
	private Supplier<BitSet> bitSetSupplier;
	private List<Storage<Integer, Integer[]>> storages;

	@Before
	public void createStorage() {
		storages = new ArrayList<>();
		bitSetSupplier = EWAHBitSet::new;
		storages.add(new StorageBucketIntegers(bitSetSupplier));
		storages.add(new StorageRange<Integer>(bitSetSupplier, Integer.class));
	}

	@Test
	public void rangeRequestTest() {
		for (Storage<Integer, Integer[]> storage : storages) {
			rangeRequestTest(storage);
		}
	}

	private void rangeRequestTest(Storage<Integer, Integer[]> storage) {
		fillUpStorage(storage);

		Random random = new Random();

		for (int i = 0; i < VALUES_COUNT; i++) {
			int from = random.nextInt(2 * VALUES_COUNT) - VALUES_COUNT / 2;
			int to = random.nextInt(2 * VALUES_COUNT) - VALUES_COUNT / 2;

			BitSet expected = bitSetSupplier.get();
			for (int j = Math.max(0, from); j <= to && j < VALUES_COUNT; j++) {
				expected = expected.set(j);
			}
			assertEquals(
					expected,
					storage.findSet(toArr(from, to)));
		}
	}

	private void fillUpStorage(Storage<Integer, Integer[]> storage) {
		for (int i = 0; i < VALUES_COUNT; i++) {
			storage.add(i, ((i)));
		}
	}

	@Test
	public void emptyResultTest() throws Exception {
		for (Storage<Integer, Integer[]> storage : storages) {
			emptyResultTest(storage);
		}
	}

	public void emptyResultTest(Storage<Integer, Integer[]> storage) throws Exception {
		fillUpStorage(storage);

		checkForEmptyResult(storage, VALUES_COUNT + 1, VALUES_COUNT + 1);
		checkForEmptyResult(storage, VALUES_COUNT, 0);
		checkForEmptyResult(storage, -2, -1);
	}

	private void checkForEmptyResult(Storage<Integer, Integer[]> storage, int from, int to) {
		assertEquals(
				0,
				storage.findSet(toArr(from, to)).cardinality());
	}
	
	private Integer[] toArr(Integer from, Integer to) {
		Integer[] result = new Integer[2];
		result[0] = from;
		result[1] = to;
		return result;
	}
}
