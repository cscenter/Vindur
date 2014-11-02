package ru.csc.vindur.test.testHelpers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.csc.vindur.EngineConfig;
import ru.csc.vindur.Request;
import ru.csc.vindur.bitset.bitsetFabric.BitSetFabric;
import ru.csc.vindur.document.Value;
import ru.csc.vindur.document.ValueType;
import ru.csc.vindur.test.DocumentGeneratorBase;
import ru.csc.vindur.test.RequestGeneratorBase;
import ru.csc.vindur.test.utils.AttributeGenerator;
import ru.csc.vindur.test.utils.RandomUtils;

public class MultyAttributesTest implements TestHelper {
	private final EngineConfig simpleEngineConfig;
	private final DocumentGeneratorBase docGenerator;
	private final RequestGeneratorBase reqGenerator;
	private final String descriptionString;
	
	public MultyAttributesTest(final int attributesCount, Map<ValueType, Double> typeFrequencies, 
			Map<ValueType, Integer> valuesCount, int documentsCount, int requestsCount, 
			final int reqAttributesCount, BitSetFabric bitSetFabric) {
		final String[] attributeNames = new String[attributesCount];
		final Value[][] attributeValues = new Value[attributesCount][];
		Map<String, ValueType> indexes = new HashMap<>(attributesCount);
		for(int i = 0; i < attributesCount; i ++) {
			attributeNames[i] = RandomUtils.getString(1, 10);
			ValueType type = RandomUtils.getFrec(typeFrequencies);
			attributeValues[i] = AttributeGenerator.generateValues(type, valuesCount.get(type));
			indexes.put(attributeNames[i], type);
		}
		simpleEngineConfig = new EngineConfig(indexes, bitSetFabric);
		
		docGenerator = new DocumentGeneratorBase(false, documentsCount) {
			@Override
			protected Map<String, List<Value>> generateDocument() {
				Map<String, List<Value>> document = new HashMap<>(attributesCount);
				double expectedValue = 0.5;
				double standartDeviation = 0.3;
				for(int i = 0; i < attributesCount; i ++) {
					Value val = RandomUtils.gaussianRandomElement(attributeValues[i], expectedValue, standartDeviation);
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
				
				double expectedValue = 0.5;
				double standartDeviation = 0.3;
				for(int idx: attributeIndexes) {
					Value val = RandomUtils.gaussianRandomElement(attributeValues[idx], expectedValue, standartDeviation);
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
