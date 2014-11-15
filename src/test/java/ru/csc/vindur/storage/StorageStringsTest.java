package ru.csc.vindur.storage;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.bitsetFabric.BitSetFabric;
import ru.csc.vindur.bitset.bitsetFabric.EWAHBitSetFabric;
import ru.csc.vindur.document.Value;

public class StorageStringsTest {
	private static final int VALUES_COUNT = 1000;
	private BitSetFabric bitSetFabric;
	private StorageStrings storageStrings;
	@Before
	public void createStorage() {
		bitSetFabric = new EWAHBitSetFabric();
		storageStrings = new StorageStrings(bitSetFabric);
	}
	
	@Test
	public void simpleTest() {
		for(int i = 0; i < VALUES_COUNT; i ++) {
			storageStrings.add(i, new Value(Integer.toString(i)));
			storageStrings.add(i + 1, new Value(Integer.toString(i)));
		}
		
		Random random = new Random();
        
        for(int i = 0; i < VALUES_COUNT; i ++) {
        	int match = random.nextInt(VALUES_COUNT);
        	BitSet expected = bitSetFabric.newInstance().set(match).set(match + 1);
            assertEquals(expected, storageStrings.findSet(Integer.toString(match)));
        }

	}
}
