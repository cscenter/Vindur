package ru.csc.vindur.test.testHelpers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import ru.csc.vindur.EngineConfig;
import ru.csc.vindur.Request;
import ru.csc.vindur.bitset.bitsetFabric.BitSetFabric;
import ru.csc.vindur.document.Value;
import ru.csc.vindur.document.StorageType;
import ru.csc.vindur.test.DocumentGeneratorBase;
import ru.csc.vindur.test.RequestGeneratorBase;
import ru.csc.vindur.test.utils.AttributeGenerator;
import ru.csc.vindur.test.utils.RandomUtils;

public class MultyAttributesTest implements TestHelper {
	private final EngineConfig simpleEngineConfig;
	private final DocumentGeneratorBase docGenerator;
	private final RequestGeneratorBase reqGenerator;
	private final String descriptionString;
	
	public MultyAttributesTest(final int attributesCount, Map<StorageType, Double> typeFrequencies, 
			Map<StorageType, Integer> valuesCount, int documentsCount, int requestsCount, 
			final int reqAttributesCount, BitSetFabric bitSetFabric, ExecutorService executorService) {
		final String[] attributeNames = new String[attributesCount];
		final Value[][] attributeValues = new Value[attributesCount][];
		Map<String, StorageType> indexes = new HashMap<>(attributesCount);
		for(int i = 0; i < attributesCount; i ++) {
			attributeNames[i] = RandomUtils.getString(1, 10);
			StorageType type = RandomUtils.getFrec(typeFrequencies);
			attributeValues[i] = AttributeGenerator.generateValues(type, valuesCount.get(type));
			indexes.put(attributeNames[i], type);
		}
		simpleEngineConfig = new EngineConfig(indexes, bitSetFabric, executorService);
		
		docGenerator = new DocumentGeneratorBase(false, documentsCount) {
			@Override
			protected Map<String, List<Value>> generateDocument() {
				Map<String, List<Value>> document = new HashMap<>(attributesCount);
				for(int i = 0; i < attributesCount; i ++) {
					Value val = RandomUtils.gaussianRandomElement(attributeValues[i], 0.5, 1.0/6);
					document.put(attributeNames[i], Arrays.asList(val));
				}
				return document;
			}
		};
		reqGenerator = new RequestGeneratorBase(false, requestsCount) {
			@Override
			protected Request generateRequest() {
				Request request = Request.build();
				Set<Integer> attributeIndexes = RandomUtils.getRandomIndexes(attributesCount, reqAttributesCount);
				for(int idx: attributeIndexes) {
					Value val = RandomUtils.gaussianRandomElement(attributeValues[idx], 0.5, 1.0/6);
					request.exact(attributeNames[idx], val.getValue());
				}
				return request;
			}
		};
		descriptionString = String.format("MultyAttributesTest [%s attributes, %s documents, %s requests, %s requested attributes]", 
				attributesCount, documentsCount, requestsCount, reqAttributesCount);
	}

	@Override
	public EngineConfig getEngineConfig() {
		return simpleEngineConfig;
	}

	@Override
	public DocumentGeneratorBase getDocumentGenerator() {
		return docGenerator;
	}

	@Override
	public RequestGeneratorBase getRequestGenerator() {
		return reqGenerator;
	}

	@Override
	public String toString() {
		return descriptionString;
	}

}
