package ru.csc.vindur.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

import ru.csc.vindur.Engine;
import ru.csc.vindur.Request;
import ru.csc.vindur.bitset.bitsetFabric.BitSetFabric;
import ru.csc.vindur.bitset.bitsetFabric.EWAHBitSetFabric;
import ru.csc.vindur.document.Value;
import ru.csc.vindur.document.StorageType;
import ru.csc.vindur.test.testHelpers.MultyAttributesTest;
import ru.csc.vindur.test.testHelpers.OneAttributeTest;
import ru.csc.vindur.test.testHelpers.TestHelper;
import ru.csc.vindur.test.utils.RandomUtils;

public class VindurEngineTest {
	private static final Logger LOG = LoggerFactory.getLogger(VindurEngineTest.class);

	public static void main(String[] args) {
		BitSetFabric fabric = new EWAHBitSetFabric();
		ExecutorService executor = Executors.newFixedThreadPool(4);
		run(new OneAttributeTest(StorageType.ENUM, 100, 1000000, 10000, fabric, executor));
		run(new OneAttributeTest(StorageType.STRING, 30000, 1000000, 100000, fabric, executor));
		run(new OneAttributeTest(StorageType.NUMERIC, 3000, 100000, 100000, fabric, executor));

		Map<StorageType, Double> typeFrequencies = new HashMap<>();
		Map<StorageType, Integer> valuesCount = new HashMap<>();
		typeFrequencies.put(StorageType.STRING, 0.4);
		typeFrequencies.put(StorageType.ENUM, 0.4);
		typeFrequencies.put(StorageType.NUMERIC, 0.2);
		valuesCount.put(StorageType.ENUM, 5);
		valuesCount.put(StorageType.STRING, 30);
		valuesCount.put(StorageType.NUMERIC, 30);
		run(new MultyAttributesTest(20, typeFrequencies, valuesCount, 
				100000, 100000, 5, fabric, executor));

		executor.shutdownNow();
	}

	private static void run(TestHelper helper) {
		LOG.info("Warm up started");
		run(helper, true);
		LOG.info("Warm up finished");
		run(helper, false);
	}
	
	// TODO warmUp flag doesn't looks good
	private static void run(TestHelper helper, boolean warmUp) {
		if(!warmUp) {
			LOG.info("Test with\n{}\nstarted", helper);
		}
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
		loadingTime.stop();
		if(!warmUp) {
			LOG.info("{} documents with {} atribute values loaded", documentGenerator.getDocumentsCount(), 
					attributesSetted);
			LOG.info("Loading time is {}", loadingTime);
			double avgTime = loadingTime.elapsed(TimeUnit.MILLISECONDS) / (double)documentGenerator.getDocumentsCount();
			LOG.info("Average time per document is {}ms", avgTime);
		}
		loadingTime = null;

		final RequestGeneratorBase requestGenerator = helper.getRequestGenerator();
		
		final BlockingQueue<Future<List<Integer>>> results = new ArrayBlockingQueue<>(100); 
		
		final AtomicLong resultsCount = new AtomicLong();
		
		Thread resultsFetcher = new Thread(new Runnable() {
			@Override
			public void run() {
				for(int i = 0; i < requestGenerator.getRequestsCount(); i++) {
					try {
						List<Integer> result = results.take().get();
						resultsCount.addAndGet(result.size());
						LOG.debug("Engine returned {} results", result.size());
					} catch (InterruptedException e) {
						LOG.error(e.getMessage());
					} catch (ExecutionException e) {
						LOG.error(e.getMessage());
					}
				}
			}
		});
		try {
			resultsFetcher.start();
			Stopwatch executingTime = Stopwatch.createStarted();
			for (Request request: requestGenerator) {
				LOG.debug("Request generated: {}", request);
				results.put(engine.executeRequestAsync(request));
			}
			resultsFetcher.join();
			executingTime.stop();
			if(!warmUp) {
				LOG.info("{} request executed", requestGenerator.getRequestsCount());
				LOG.info("Executing time is {}. Engine returned {} results", executingTime, resultsCount);
				double avgTime = executingTime.elapsed(TimeUnit.MILLISECONDS) / (double)requestGenerator.getRequestsCount();
				LOG.info("Average time per request is {}ms", avgTime);
				double avgResults = resultsCount.longValue() / (double)requestGenerator.getRequestsCount();
				LOG.info("Average results per request is {}", avgResults);
			}
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
		}
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
