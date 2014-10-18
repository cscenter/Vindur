package ru.csc.vindur.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.csc.vindur.Engine;
import ru.csc.vindur.IndexStrings;
import ru.csc.vindur.Request;
import ru.csc.vindur.entity.Entity;
import ru.csc.vindur.entity.Value;

public class VindurEngineTest {
	private static final Logger LOG = LoggerFactory.getLogger(VindurEngineTest.class);
	private static final int MAX_SIZE = 10000;
	private static final int ENTITIES_COUNT = MAX_SIZE;
	private static final int ASPECTS_COUNT = 200;

	public static void main(String[] args) {
		run();
	}

	private static void run() {
		Engine engine = new Engine(MAX_SIZE);
		EntitiesDescriptor entitiesDesctoptor = new EntitiesDescriptor();
		
		{
			List<AspectDescriptor> aspectDescriptors = entitiesDesctoptor.getAspectDescriptors();
			for(int i = 0; i < ASPECTS_COUNT; i ++) {
				AspectDescriptor aspectDescriptor = 
						new StringAspectDescriptor(String.valueOf(i), 10, 100);
				aspectDescriptors.add(aspectDescriptor);
				engine.addIndex(aspectDescriptor.getName(), new IndexStrings(MAX_SIZE));
			}
		}
		
		EntityGeneratorBase entityGenerator = 
				new EntityGenerator(false, ENTITIES_COUNT, entitiesDesctoptor);
		
		
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

}
