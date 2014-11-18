package ru.csc.vindur.storage;

import org.junit.Before;
import org.junit.Test;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.EWAHBitSet;
import ru.csc.vindur.bitset.ROBitSet;
import ru.csc.vindur.document.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

public class IntegerRangeStoragesTest {
	private static final int VALUES_COUNT = 1000;
	private Supplier<BitSet> bitSetSupplier;
	private List<Storage> storages;

	@Before
	public void createStorage() {
		storages = new ArrayList<>();
		bitSetSupplier = EWAHBitSet::new;
		storages.add(new StorageBucketIntegers(bitSetSupplier));
		storages.add(new StorageIntegers(bitSetSupplier));
	}

	@Test
	public void exactRequestTest() throws Exception {
		for (Storage storage : storages) {
			exactRequestTest(storage);
		}
	}

	private void exactRequestTest(Storage storage) throws Exception {
		fillUpStorage(storage);

		for (int i = 0; i < VALUES_COUNT; i++) {
			assertEquals(bitSetSupplier.get().set(i),
					((ExactStorage) storage).findSet(Integer.toString(i)));
		}

		Random random = new Random();

		for (int i = 0; i < VALUES_COUNT; i++) {
			int match = random.nextInt(VALUES_COUNT);
			BitSet expected = bitSetSupplier.get().set(match);
			assertEquals(expected,
					((ExactStorage) storage).findSet(Integer.toString(match)));
		}

	}

	@Test
	public void rangeRequestTest() {
		for (Storage storage : storages) {
			rangeRequestTest(storage);
		}
	}

	private void rangeRequestTest(Storage storage) {
		fillUpStorage(storage);

		Random random = new Random();

		for (int i = 0; i < VALUES_COUNT; i++) {
			int match = random.nextInt(VALUES_COUNT);
			ROBitSet expected = bitSetSupplier.get().set(match);
			ROBitSet actual = ((RangeStorage) storage).findRangeSet(
					Integer.toString(match), Integer.toString(match));
			assertEquals(expected, actual);
		}

		for (int i = 0; i < VALUES_COUNT; i++) {
			int from = random.nextInt(2 * VALUES_COUNT) - VALUES_COUNT / 2;
			int to = random.nextInt(2 * VALUES_COUNT) - VALUES_COUNT / 2;

			BitSet expected = bitSetSupplier.get();
			for (int j = Math.max(0, from); j <= to && j < VALUES_COUNT; j++) {
				expected = expected.set(j);
			}
			assertEquals(
					expected,
					((RangeStorage) storage).findRangeSet(
							Integer.toString(from), Integer.toString(to)));
		}
	}

	private void fillUpStorage(Storage storage) {
		for (int i = 0; i < VALUES_COUNT; i++) {
			storage.add(i, new Value(Integer.toString(i)));
		}
	}

	@Test
	public void emptyResultTest() throws Exception {
		for (Storage storage : storages) {
			emptyResultTest(storage);
		}
	}

	public void emptyResultTest(Storage storage) throws Exception {
		fillUpStorage(storage);

		checkForEmptyResult(storage, VALUES_COUNT + 1, VALUES_COUNT + 1);
		checkForEmptyResult(storage, VALUES_COUNT, 0);
		checkForEmptyResult(storage, -2, -1);
		checkForEmptyResult(storage, VALUES_COUNT + 1);
		checkForEmptyResult(storage, -1);
	}

	private void checkForEmptyResult(Storage storage, int from, int to) {
		assertEquals(
				0,
				((RangeStorage) storage).findRangeSet(Integer.toString(from),
						Integer.toString(to)).cardinality());
	}

	private void checkForEmptyResult(Storage storage, int match)
			throws Exception {
		assertEquals(0,
				((ExactStorage) storage).findSet(Integer.toString(match))
						.cardinality());
	}
}
