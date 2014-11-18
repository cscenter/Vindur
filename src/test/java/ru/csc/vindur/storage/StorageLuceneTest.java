package ru.csc.vindur.storage;

import org.junit.Before;
import org.junit.Test;
import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.EWAHBitSet;
import ru.csc.vindur.document.Value;

import static org.junit.Assert.assertEquals;

public class StorageLuceneTest
{
	private StorageLucene storageLucene;
	@Before
	public void createStorage() {
		storageLucene = new StorageLucene(EWAHBitSet::new);
	}
	
	@Test
	public void simpleTest() {
		storageLucene.add(0, new Value("value0"));
		storageLucene.add(1, new Value("value1"));
		storageLucene.add(2, new Value("value0 value1"));
		
		BitSet expected = new EWAHBitSet().set(0).set(2);
		assertEquals(expected, storageLucene.findSet("value0"));
		expected = new EWAHBitSet().set(1).set(2);
		assertEquals(expected, storageLucene.findSet("value1"));
	}
	
	@Test
	public void wildcardSearchTest() {
		storageLucene.add(0, new Value("value0"));
		
		BitSet expected = new EWAHBitSet().set(0);
		assertEquals(expected, storageLucene.findSet("v?lu*"));
	}
	
	@Test
	public void regExpSearchTest() {
		storageLucene.add(0, new Value("value0"));
		
		BitSet expected = new EWAHBitSet().set(0);
		assertEquals(expected, storageLucene.findSet("/[fvs]alue./"));
	}

	@Test(expected = RuntimeException.class)
	public void incorrectWildCardTest() {
		storageLucene.findSet("*lue0");
	}

	@Test(expected = RuntimeException.class)
	public void incorrectRegExp1Test() {
		storageLucene.findSet("/value0");
	}
	
	@Test(expected = RuntimeException.class)
	public void incorrectRegExp2Test() {
		storageLucene.findSet("/1/1/");
	}
}
