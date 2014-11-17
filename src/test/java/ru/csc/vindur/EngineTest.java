package ru.csc.vindur;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import ru.csc.vindur.bitset.bitsetFabric.BitSetFabric;
import ru.csc.vindur.bitset.bitsetFabric.EWAHBitSetFabric;
import ru.csc.vindur.document.StorageType;
import ru.csc.vindur.document.Value;
import ru.csc.vindur.optimizer.TinyOptimizer;

public class EngineTest {
	private static final String INT_ATTR = "Int";
	private static final String STR_ATTR = "Str";

	@Test
	public void simpleTest() throws Exception {
		Map<String, StorageType> indexes = new HashMap<>();
		indexes.put(STR_ATTR, StorageType.STRING);
		indexes.put(INT_ATTR, StorageType.NUMERIC);
		BitSetFabric bitSetFabric = new EWAHBitSetFabric();
		EngineConfig config = new EngineConfig(indexes , bitSetFabric, new TinyOptimizer());
		Engine engine = new Engine(config);

		int doc1 = engine.createDocument();
		int doc2 = engine.createDocument();
		int doc3 = engine.createDocument();
		int doc4 = engine.createDocument();

		engine.setAttributeByDocId(doc1, STR_ATTR, new Value("value1"));
		engine.setAttributeByDocId(doc2, STR_ATTR, new Value("value1"));
		engine.setAttributeByDocId(doc3, STR_ATTR, new Value("value2"));
		engine.setAttributeByDocId(doc4, STR_ATTR, new Value("value2"));
		engine.setAttributeByDocId(doc1, INT_ATTR, new Value("1"));
		engine.setAttributeByDocId(doc2, INT_ATTR, new Value("2"));
		engine.setAttributeByDocId(doc3, INT_ATTR, new Value("2"));
		engine.setAttributeByDocId(doc4, INT_ATTR, new Value("2"));

		Request r1 = Request.build().exact(STR_ATTR, "value1").exact(INT_ATTR, "2");
		assertEquals(Arrays.asList(doc2), engine.executeRequest(r1));
		
		Request r2 = Request.build().exact(STR_ATTR, "value1").range(INT_ATTR, "1", "2");
		assertEquals(Arrays.asList(doc1, doc2), engine.executeRequest(r2));
		
		Request r3 = Request.build().exact(STR_ATTR, "value2").range(INT_ATTR, "2", "2");
		assertEquals(Arrays.asList(doc3, doc4), engine.executeRequest(r3));

		Request r4 = Request.build().exact(INT_ATTR, "3");
		assertEquals(Arrays.asList(), engine.executeRequest(r4));

		Request r5 = Request.build().exact(INT_ATTR, "1").exact(STR_ATTR, "value2");
		assertEquals(Arrays.asList(), engine.executeRequest(r5));
	}
}
