package ru.csc.vindur;

import org.junit.Test;

import ru.csc.vindur.bitset.EWAHBitSet;
import ru.csc.vindur.optimizer.DumbOptimizer;
import ru.csc.vindur.storage.StorageType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class EngineTest {
	private static final String INT_ATTR = "Int";
	private static final String STR_ATTR = "Str";

	@Test
	public void simpleTest() throws Exception {
		Map<String, StorageType> indexes = new HashMap<>();
		indexes.put(STR_ATTR, StorageType.STRING);
		indexes.put(INT_ATTR, StorageType.NUMERIC);
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

		Request r1 = Request.build().request(STR_ATTR, "value1").request(INT_ATTR, toArr(2, 2));
		assertEquals(Arrays.asList(doc2), engine.executeRequest(r1));
		
		Request r2 = Request.build().request(STR_ATTR, "value1").request(INT_ATTR, toArr(1, 2));
		assertEquals(Arrays.asList(doc1, doc2), engine.executeRequest(r2));
		
		Request r3 = Request.build().request(STR_ATTR, "value2").request(INT_ATTR, toArr(2, 2));
		assertEquals(Arrays.asList(doc3, doc4), engine.executeRequest(r3));

		Request r4 = Request.build().request(INT_ATTR, toArr(3, 3));
		assertEquals(Arrays.asList(), engine.executeRequest(r4));

		Request r5 = Request.build().request(INT_ATTR, toArr(1, 1)).request(STR_ATTR, "value2");
		assertEquals(Arrays.asList(), engine.executeRequest(r5));
	}
	
	private Integer[] toArr(Integer from, Integer to) {
		Integer[] result = new Integer[2];
		result[0] = from;
		result[1] = to;
		return result;
	}
}
