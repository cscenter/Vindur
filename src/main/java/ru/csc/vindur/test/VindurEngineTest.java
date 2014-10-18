package ru.csc.vindur.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.csc.vindur.Engine;
import ru.csc.vindur.Request;
import ru.csc.vindur.Document;

public class VindurEngineTest {
	private static final Logger LOG = LoggerFactory.getLogger(VindurEngineTest.class);

	public static void main(String[] args) {
		run();
	}

	// TODO remove this after fixing this method
	@SuppressWarnings("null")
	private static void run() {
		// TODO create engine configuration and engine
		Engine engine = null;
		// TODO get entity generator
		DocumentGeneratorBase entityGenerator = null;
		
		
		for (Document entity: entityGenerator)
		{
			LOG.debug("Entity generated: {}", entity);
			int docId = engine.createDocument();
			loadEntity(engine, entity, docId);
		}

		// TODO get request generator
		RequestGeneratorBase requestGenerator = null;
		
		for (Request request: requestGenerator)
		{
			LOG.debug("Request generated: {}", request);
			engine.executeRequest(request);
		}
	}

	private static void loadEntity(Engine engine, Document entity, int docId) {
		// TODO
	}

}
