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
	private List<StorageBase<Integer, RangeRequest>> storages;

	@Before
	public void createStorage() {
		storages = new ArrayList<>();
		bitSetSupplier = EWAHBitSet::new;
		storages.add(new StorageBucketIntegers(bitSetSupplier));
		storages.add(new StorageRange<Integer>(bitSetSupplier, Integer.class));
	}

	@Test
	public void rangeRequestTest() {
		for (StorageBase<Integer, RangeRequest> storage : storages) {
			rangeRequestTest(storage);
		}
	}

	private void rangeRequestTest(StorageBase<Integer, RangeRequest> storage) {
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
					storage.findSet(toRequest(from, to)));
		}
	}

	private void fillUpStorage(StorageBase<Integer, RangeRequest> storage) {
		for (int i = 0; i < VALUES_COUNT; i++) {
			storage.add(i, ((i)));
		}
	}

	@Test
	public void emptyResultTest() throws Exception {
		for (StorageBase<Integer, RangeRequest> storage : storages) {
			emptyResultTest(storage);
		}
	}

	public void emptyResultTest(StorageBase<Integer, RangeRequest> storage) throws Exception {
		fillUpStorage(storage);

		checkForEmptyResult(storage, VALUES_COUNT + 1, VALUES_COUNT + 1);
		checkForEmptyResult(storage, VALUES_COUNT, 0);
		checkForEmptyResult(storage, -2, -1);
	}

	private void checkForEmptyResult(StorageBase<Integer, RangeRequest> storage, int from, int to) {
		assertEquals(
				0,
				storage.findSet(toRequest(from, to)).cardinality());
	}
	
	private RangeRequest toRequest(Integer from, Integer to) {
		return StorageRangeBase.generateRequest(from, to);
	}
	
	@Test
	public void checkValueTest() {
		for (StorageBase<Integer, RangeRequest> storage : storages) {
			storage.add(0, 10);
			assertEquals(false, storage.checkValue(0, 10, toRequest(9, 9)));
			assertEquals(false, storage.checkValue(0, 10, toRequest(-123, 9)));
			assertEquals(false, storage.checkValue(0, 10, toRequest(99, 999)));
			assertEquals(false, storage.checkValue(0, 10, toRequest(12, 21)));
			assertEquals(true, storage.checkValue(0, 10, toRequest(0, 21)));
			assertEquals(true, storage.checkValue(0, 10, toRequest(0, 11)));
			assertEquals(true, storage.checkValue(0, 10, toRequest(10, 121)));
			assertEquals(true, storage.checkValue(0, 10, toRequest(-123, 10)));
		}
	}
}
