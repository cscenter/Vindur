package ru.csc.vindur.storage;

import static org.junit.Assert.*;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Before;
import org.junit.Test;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.bitsetFabric.BitSetFabric;
import ru.csc.vindur.bitset.bitsetFabric.EWAHBitSetFabric;
import ru.csc.vindur.document.Value;

public class StorageLuceneTest {
	private BitSetFabric bitSetFabric;
	private StorageLucene storageLucene;
	@Before
	public void createStorage() {
		bitSetFabric = new EWAHBitSetFabric();
		storageLucene = new StorageLucene(bitSetFabric);
	}
	
	@Test
	public void simpleTest() throws ParseException {
		storageLucene.add(0, new Value("value0"));
		storageLucene.add(1, new Value("value1"));
		storageLucene.add(2, new Value("value0 value1"));
		
		BitSet expected = bitSetFabric.newInstance().set(0).set(2);
		assertEquals(expected, storageLucene.findSet("value0"));
		expected = bitSetFabric.newInstance().set(1).set(2);
		assertEquals(expected, storageLucene.findSet("value1"));
	}
	
	@Test
	public void wildcardSearchTest() throws ParseException {
		storageLucene.add(0, new Value("value0"));
		
		BitSet expected = bitSetFabric.newInstance().set(0);
		assertEquals(expected, storageLucene.findSet("v?lu*"));
	}
	
	@Test
	public void regExpSearchTest() throws ParseException {
		storageLucene.add(0, new Value("value0"));
		
		BitSet expected = bitSetFabric.newInstance().set(0);
		assertEquals(expected, storageLucene.findSet("/[fvs]alue./"));
	}

	@Test(expected = ParseException.class)
	public void incorrectWildCardTest() throws ParseException {
		storageLucene.findSet("*lue0");
	}

	@Test(expected = ParseException.class)
	public void incorrectRegExp1Test() throws ParseException {
		storageLucene.findSet("/value0");
	}
	
	@Test(expected = ParseException.class)
	public void incorrectRegExp2Test() throws ParseException {
		storageLucene.findSet("/1/1/");
	}
}
