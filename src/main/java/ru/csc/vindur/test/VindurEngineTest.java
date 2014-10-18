package ru.csc.vindur.test;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

import ru.csc.vindur.Engine;
import ru.csc.vindur.Request;
import ru.csc.vindur.Value;
import ru.csc.vindur.test.testHelper.TestHelper;

public class VindurEngineTest {
	private static final Logger LOG = LoggerFactory.getLogger(VindurEngineTest.class);

	public static void main(String[] args) {
		run();
	}

	// TODO rewrite time counting
	private static void run() {
		TestHelper helper = new TestHelper();
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
			for(Value value: attribute.getValue()) {
				engine.setAttributeByDocId(docId, attribute.getKey(), value);
				settedAttributes += 1;
			}
		}
		return settedAttributes;
	}

}
