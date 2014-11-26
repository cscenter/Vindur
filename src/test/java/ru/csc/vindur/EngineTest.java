package ru.csc.vindur;

import org.junit.Test;

import ru.csc.vindur.bitset.EWAHBitSet;
import ru.csc.vindur.optimizer.DumbOptimizer;
import ru.csc.vindur.storage.StorageLucene;
import ru.csc.vindur.storage.StorageType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class EngineTest {
	private static final String INT_ATTR = "Int";
	private static final String STR_ATTR = "Str";
	private static final String STR_ATTR2 = "SortedStr";
	private static final String STR_ATTR3 = "LuceneStr";

	@Test
	public void simpleTest() throws Exception {
		Map<String, StorageType> indexes = new HashMap<>();
		indexes.put(STR_ATTR, StorageType.STRING);
		indexes.put(INT_ATTR, StorageType.RANGE_INTEGER);
		indexes.put(STR_ATTR2, StorageType.RANGE_STRING);
		indexes.put(STR_ATTR3, StorageType.LUCENE_STRING);
		EngineConfig config = new EngineConfig(indexes , EWAHBitSet::new, new DumbOptimizer());
		Engine engine = new Engine(config);

		int doc1 = engine.createDocument();
		int doc2 = engine.createDocument();
		int doc3 = engine.createDocument();
		int doc4 = engine.createDocument();

		engine.setAttributeByDocId(doc1, STR_ATTR, ("value1"));
		engine.setAttributeByDocId(doc2, STR_ATTR, ("value1"));
		engine.setAttributeByDocId(doc3, STR_ATTR, ("value2"));
		engine.setAttributeByDocId(doc4, STR_ATTR, ("value2"));
		engine.setAttributeByDocId(doc1, INT_ATTR, 1);
		engine.setAttributeByDocId(doc2, INT_ATTR, 2);
		engine.setAttributeByDocId(doc3, INT_ATTR, 2);
		engine.setAttributeByDocId(doc4, INT_ATTR, 2);
		engine.setAttributeByDocId(doc1, STR_ATTR2, "abc");
		engine.setAttributeByDocId(doc2, STR_ATTR2, "aa");
		engine.setAttributeByDocId(doc3, STR_ATTR2, "b");
		engine.setAttributeByDocId(doc4, STR_ATTR2, "zxcvbnm");
		engine.setAttributeByDocId(doc1, STR_ATTR3, "aa bb cc");
		engine.setAttributeByDocId(doc2, STR_ATTR3, "aa");
		engine.setAttributeByDocId(doc3, STR_ATTR3, "bb");
		engine.setAttributeByDocId(doc4, STR_ATTR3, "cc bb");

		Request r1 = Request.build().request(STR_ATTR, "value1").request(INT_ATTR, Arrays.asList(2, 2).toArray());
		assertEquals(Arrays.asList(doc2), engine.executeRequest(r1));
		
		Request r2 = Request.build().request(STR_ATTR, "value1").request(INT_ATTR, Arrays.asList(1, 2).toArray());
		assertEquals(Arrays.asList(doc1, doc2), engine.executeRequest(r2));
		
		Request r3 = Request.build().request(STR_ATTR, "value2").request(INT_ATTR, Arrays.asList(2, 2).toArray());
		assertEquals(Arrays.asList(doc3, doc4), engine.executeRequest(r3));

		Request r4 = Request.build().request(INT_ATTR, Arrays.asList(3, 3).toArray());
		assertEquals(Arrays.asList(), engine.executeRequest(r4));

		Request r5 = Request.build().request(INT_ATTR, Arrays.asList(1, 1).toArray()).request(STR_ATTR, "value2");
		assertEquals(Arrays.asList(), engine.executeRequest(r5));

		Request r6 = Request.build().request(STR_ATTR2, Arrays.asList("abc", "zxcvbnm").toArray());
		assertEquals(Arrays.asList(doc1, doc3, doc4), engine.executeRequest(r6));

		Request r7 = Request.build().request(STR_ATTR3, StorageLucene.generateRequest("aa"));
		assertEquals(Arrays.asList(doc1, doc2), engine.executeRequest(r7));

		Request r8 = Request.build().request(STR_ATTR3, StorageLucene.generateRequest("b*"));
		assertEquals(Arrays.asList(doc1, doc3, doc4), engine.executeRequest(r8));
	}
}
