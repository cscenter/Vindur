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
    public void exactRequestTest() {
		for(RangeStorage storage: storages) {
			exactRequestTest(storage);
		}
    }

	private void exactRequestTest(RangeStorage storage) {
		fillUpStorage(storage);

        for(int i = 0; i < VALUES_COUNT; i ++) {
        	assertEquals(bitSetFabric.newInstance().set(i), storage.findSet(Integer.toString(i)));
        }

        Random random = new Random();
        
        for(int i = 0; i < VALUES_COUNT; i ++) {
        	int match = random.nextInt(VALUES_COUNT);
        	BitSet expected = bitSetFabric.newInstance().set(match);
            assertEquals(expected, storage.findSet(Integer.toString(match)));
        }

	}

	@Test
	public void rangeRequestTest() {
		for(RangeStorage storage: storages) {
			rangeRequestTest(storage);
		}
	}
	
	private void rangeRequestTest(RangeStorage storage) {
		fillUpStorage(storage);

        Random random = new Random();
        
        for(int i = 0; i < VALUES_COUNT; i ++) {
        	int match = random.nextInt(VALUES_COUNT);
        	BitSet expected = bitSetFabric.newInstance().set(match);
        	BitSet actual = storage.findRangeSet(Integer.toString(match), Integer.toString(match));
            assertEquals(expected, actual);
        }

        for(int i = 0; i < VALUES_COUNT; i ++) {
			int from = random.nextInt(2 * VALUES_COUNT) - VALUES_COUNT / 2;
			int to = random.nextInt(2 * VALUES_COUNT) - VALUES_COUNT / 2;

			BitSet expected = bitSetFabric.newInstance();
			for(int j = Math.max(0, from); j <= to && j < VALUES_COUNT; j ++) {
				expected = expected.set(j);
			}
			assertEquals(expected, storage.findRangeSet(Integer.toString(from), Integer.toString(to)));
        }
	}

	private void fillUpStorage(RangeStorage storage) {
		for(int i = 0; i < VALUES_COUNT; i ++) {
				storage.add(i, new Value(Integer.toString(i)));
		}
	}

    @Test
    public void emptyResultTest() {
        for(RangeStorage storage: storages) {
        	emptyResultTest(storage);
        }
    }
    
    public void emptyResultTest(RangeStorage storage) {
    	fillUpStorage(storage);

    	checkForEmptyResult(storage, VALUES_COUNT + 1, VALUES_COUNT + 1);
    	checkForEmptyResult(storage, VALUES_COUNT, 0);
    	checkForEmptyResult(storage, -2, -1);
    	checkForEmptyResult(storage, VALUES_COUNT + 1);
    	checkForEmptyResult(storage, -1);
    }

    private void checkForEmptyResult(RangeStorage storage, int from, int to) {
    	assertEquals(0, storage.findRangeSet(Integer.toString(from), Integer.toString(to)).cardinality());
    }
    private void checkForEmptyResult(RangeStorage storage, int match) {
    	assertEquals(0, storage.findSet(Integer.toString(match)).cardinality());
    }
}
