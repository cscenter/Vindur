package ru.csc.vindur.storage;

import org.junit.Before;
import org.junit.Test;
import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.EWAHBitSet;

import java.util.Random;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

public class StorageStringsTest
{
	private static final int VALUES_COUNT = 1000;
	private Supplier<BitSet> bitSetSupplier;
	private StorageExact<String> storageStrings;

	@Before
	public void createStorage()
    {
		bitSetSupplier = EWAHBitSet::new;
		storageStrings = new StorageExact<String>(bitSetSupplier, String.class);
	}
	
	@Test
	public void simpleTest() {
		for(int i = 0; i < VALUES_COUNT; i ++) {
			storageStrings.add(i, (Integer.toString(i)));
			storageStrings.add(i + 1, (Integer.toString(i)));
		}
		
		Random random = new Random();
        
        for(int i = 0; i < VALUES_COUNT; i ++) {
        	int match = random.nextInt(VALUES_COUNT);
        	BitSet expected = bitSetSupplier.get().set(match).set(match + 1);
            assertEquals(expected, storageStrings.findSet(Integer.toString(match)));
        }

	}
	
	@Test
	public void checkValueTest() {
		storageStrings.add(0, "value");
		assertEquals(false, storageStrings.checkValue(0, "value", "123"));
		assertEquals(false, storageStrings.checkValue(0, "value", "321"));
		assertEquals(true, storageStrings.checkValue(0, "value", "value"));
	}
}
