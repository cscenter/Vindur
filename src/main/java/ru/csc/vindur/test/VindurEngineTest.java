package ru.csc.vindur.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

import ru.csc.vindur.Engine;
import ru.csc.vindur.Request;
import ru.csc.vindur.bitset.bitsetFabric.BitSetFabric;
import ru.csc.vindur.bitset.bitsetFabric.EWAHBitSetFabric;
import ru.csc.vindur.document.Value;
import ru.csc.vindur.document.ValueType;
import ru.csc.vindur.test.testHelpers.MultyAttributesTest;
import ru.csc.vindur.test.testHelpers.OneAttributeTest;
import ru.csc.vindur.test.testHelpers.TestHelper;
import ru.csc.vindur.test.utils.RandomUtils;

public class VindurEngineTest {
	private static final Logger LOG = LoggerFactory.getLogger(VindurEngineTest.class);

	public static void main(String[] args) {
		BitSetFabric fabric = new EWAHBitSetFabric();
		// TODO warm up somehow
		run(new OneAttributeTest(ValueType.ENUM, 3, 0xFFFFF, 0xFFF, fabric));
		run(new OneAttributeTest(ValueType.STRING, 30, 0xFFFFF, 0xFFF, fabric));
		run(new OneAttributeTest(ValueType.NUMERIC, 30, 0xFFFF, 0xFF, fabric));

		Map<ValueType, Double> typeFrequencies;
		Map<ValueType, Integer> valuesCount;
		typeFrequencies = new HashMap<>();
		valuesCount = new HashMap<>();
		typeFrequencies.put(ValueType.STRING, 1.0);
		valuesCount.put(ValueType.STRING, 5);
		run(new MultyAttributesTest(20, typeFrequencies, valuesCount, 
				 0x4FFFF, 0xFFF, 7, fabric));

		typeFrequencies.put(ValueType.STRING, 0.5);
		typeFrequencies.put(ValueType.ENUM, 0.4);
		typeFrequencies.put(ValueType.NUMERIC, 0.1);
		valuesCount.put(ValueType.ENUM, 5);
		valuesCount.put(ValueType.STRING, 50);
		valuesCount.put(ValueType.NUMERIC, 50);
		run(new MultyAttributesTest(30, typeFrequencies, valuesCount, 
				 0x4FFFF, 0xFFF, 7, fabric));
	}

	private static void run(TestHelper helper) {
		LOG.info("Test with\n{}\nstarted", helper);
		RandomUtils.setSeed(0);
		Engine engine = new Engine(helper.getEngineConfig());
		DocumentGeneratorBase documentGenerator = helper.getDocumentGenerator();
		
		Stopwatch loadingTime = Stopwatch.createStarted();
		long attributesSetted = 0;
		for (Map<String, List<Value>> document: documentGenerator) {
			LOG.debug("Document generated: {}", document);
			int docId = engine.createDocument();
			attributesSetted += loadDocument(engine, document, docId);
		}
		LOG.info("{} documents with {} atribute values loaded", documentGenerator.getDocumentsCount(), 
				attributesSetted);
		LOG.info("Loading time is {}", loadingTime.stop());
		loadingTime = null;

		RequestGeneratorBase requestGenerator = helper.getRequestGenerator();

		Stopwatch executingTime = Stopwatch.createStarted();
		long resultsCount = 0;
		for (Request request: requestGenerator) {
			LOG.debug("Request generated: {}", request);
			List<Integer> result = engine.executeRequest(request);
			LOG.debug("Engine returned {} results", result.size());
			resultsCount += result.size();
		}
		LOG.info("{} request executed", requestGenerator.getRequestsCount());
		LOG.info("Executing time is {}. Engine returned {} results", executingTime.stop(), resultsCount);
	}

	private static long loadDocument(Engine engine, Map<String, List<Value>> document, int docId) {
		long settedAttributes = 0;
		for(Entry<String, List<Value>> attribute: document.entrySet()) {
			settedAttributes += attribute.getValue().size();
			for(Value value: attribute.getValue()) {
				engine.setAttributeByDocId(docId, attribute.getKey(), value);
			}
		}
		return settedAttributes;
	}

}
