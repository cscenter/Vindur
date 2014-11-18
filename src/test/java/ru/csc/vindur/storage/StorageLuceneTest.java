package ru.csc.vindur.storage;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.junit.Before;
import org.junit.Test;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.EWAHBitSet;
import ru.csc.vindur.document.Value;
import static org.junit.Assert.assertEquals;

public class StorageLuceneTest {
	private StorageLucene storageLucene;

	@Before
	public void createStorage() {
		storageLucene = new StorageLucene(EWAHBitSet::new);
	}

	@Test
	public void simpleTest() throws ParseException {
		storageLucene.add(0, new Value("value0"));
		storageLucene.add(1, new Value("value1"));
		storageLucene.add(2, new Value("value0 value1"));

		BitSet expected = new EWAHBitSet().set(0).set(2);
		assertEquals(expected, storageLucene.findSet(new QueryParser("text",
				new WhitespaceAnalyzer()).parse("value0")));
		expected = new EWAHBitSet().set(1).set(2);
		assertEquals(expected, storageLucene.findSet(new QueryParser("text",
				new WhitespaceAnalyzer()).parse("value1")));
	}

	@Test
	public void wildcardSearchTest() throws ParseException {
		storageLucene.add(0, new Value("value0"));

		BitSet expected = new EWAHBitSet().set(0);
		assertEquals(expected, storageLucene.findSet(new QueryParser("text",
				new WhitespaceAnalyzer()).parse("v?lu*")));
	}

	@Test
	public void regExpSearchTest() throws ParseException {
		storageLucene.add(0, new Value("value0"));

		BitSet expected = new EWAHBitSet().set(0);
		assertEquals(expected, storageLucene.findSet(new QueryParser("text",
				new WhitespaceAnalyzer()).parse("/[fvs]alue./")));
	}
}
