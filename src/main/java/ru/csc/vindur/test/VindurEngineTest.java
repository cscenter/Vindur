package ru.csc.vindur.test;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.csc.vindur.Engine;
import ru.csc.vindur.Request;
import ru.csc.vindur.entity.Entity;
import ru.csc.vindur.entity.Value;

public class VindurEngineTest {
	private static final Logger LOG = LoggerFactory.getLogger(VindurEngineTest.class);
	private static final int MAX_SIZE = 100000;

	public static void main(String[] args) {
		run();
	}

	private static void run() {
		EntityGeneratorBase entityGenerator = createGenerator();
		
		Engine engine = createEngine();
		
		for (Entity entity: entityGenerator)
		{
			LOG.debug("Entity generated: {}", entity);
			int docId = engine.createDocument();
			loadEntity(engine, entity, docId);
		}
		
		RequestGeneratorBase requestGenerator = new MockedRequestGenerator();
		
		for (Request request: requestGenerator)
		{
			LOG.debug("Request generated: {}", request);
			engine.executeRequest(request);
		}
	}

	private static void loadEntity(Engine engine, Entity entity, int docId) {
		for(Entry<String, ArrayList<Value>> aspectVals: entity.getValues().entrySet())
		{
			for(Value val: aspectVals.getValue())
			{
				engine.addValueByDocId(docId, aspectVals.getKey(), val);
			}
		}
	}

	private static Engine createEngine() {
		// TODO configure engine. Create indexes
		Engine engine = new Engine(MAX_SIZE);
		return engine;
	}

	private static EntityGeneratorBase createGenerator() {
		// TODO choose, create and configure generator
		EntityGeneratorBase generator = new MockedEntityGenerator();
		return generator;
	}

}
