package ru.csc.vindur.storage;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    public void simpleRequestTest() {
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

        int val = new Random().nextInt(VALUES_COUNT);

        BitSet expected = bitSetFabric.newInstance();
        expected.set(val);
        for(RangeStorage storage: storages) {
            assertEquals(expected, storage.findSet(Integer.toString(val)));
        }
    }

	@Test
	public void simpleRangeRequestTest() {
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

    @Test
    public void emptyResultTest()
    {
        for(int i = 0; i < VALUES_COUNT; i++)
        {
            for(RangeStorage storage: storages) {
                storage.add(i, new Value(Integer.toString(i)));
            }
        }

        for(int i = 0; i < VALUES_COUNT; i++)
        {
            for(RangeStorage storage: storages) {
                assertEquals(bitSetFabric.newInstance().set(i), storage.findSet(Integer.toString(i)));
            }
        }

        int from = VALUES_COUNT + 1;
        int to = VALUES_COUNT + 1;

        BitSet expected = bitSetFabric.newInstance();

        for(RangeStorage storage: storages) {
            assertEquals(expected, storage.findRangeSet(Integer.toString(from), Integer.toString(to)));
        }

        for(RangeStorage storage: storages) {
            assertEquals(expected, storage.findSet(Integer.toString(from)));
        }
    }
}
