package ru.csc.vindur.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.csc.vindur.Engine;
import ru.csc.vindur.Request;
import ru.csc.vindur.Document;
import ru.csc.vindur.Value;
import ru.csc.vindur.test.testHelper.TestHelper;

public class VindurEngineTest {
	private static final Logger LOG = LoggerFactory.getLogger(VindurEngineTest.class);

	public static void main(String[] args) {
		run();
	}

	private static void run() {
		TestHelper helper = new TestHelper();
		Engine engine = new Engine(helper.getEngineConfig());
		DocumentGeneratorBase documentGenerator = helper.getDocumentGenerator();
		
		
		for (Document document: documentGenerator) {
			LOG.debug("Document generated: {}", document);
			int docId = engine.createDocument();
			loadDocument(engine, document, docId);
		}

		RequestGeneratorBase requestGenerator = helper.getRequestGenerator();
		
		for (Request request: requestGenerator) {
			LOG.debug("Request generated: {}", request);
			engine.executeRequest(request);
		}
	}

	private static void loadDocument(Engine engine, Document document, int docId) {
		for(String attribute: document.getAttributes()) {
			for(Value value: document.getValues(attribute)) {
				engine.setAttributeByDocId(docId, attribute, value);
			}
		}
	}

}
