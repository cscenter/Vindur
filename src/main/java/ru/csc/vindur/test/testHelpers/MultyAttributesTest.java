package ru.csc.vindur.test.testHelpers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.csc.vindur.EngineConfig;
import ru.csc.vindur.Request;
import ru.csc.vindur.document.Value;
import ru.csc.vindur.document.ValueType;
import ru.csc.vindur.test.DocumentGeneratorBase;
import ru.csc.vindur.test.RequestGeneratorBase;
import ru.csc.vindur.test.utils.AttributeGenerator;
import ru.csc.vindur.test.utils.RandomUtils;

public class MultyAttributesTest implements TestHelper {
	private final int documentsCount;
	private final int requestsCount;
	private final int attributesCount;
	private final int reqAttributesCount;
	private final String[] attributeNames;
	private final Value[][] attributeValues;
	private final EngineConfig simpleEngineConfig;
	
	public MultyAttributesTest(int attributesCount, Map<ValueType, Double> typeFrequencies, 
			Map<ValueType, Integer> valuesCount, int documentsCount, int requestsCount, int reqAttributesCount) {
		this.documentsCount = documentsCount;
		this.requestsCount = requestsCount;
		this.attributesCount = attributesCount;
		this.reqAttributesCount = reqAttributesCount;
		attributeNames = new String[attributesCount];
		attributeValues = new Value[attributesCount][];
		Map<String, ValueType> indexes = new HashMap<>(attributesCount);
		
		for(int i = 0; i < attributesCount; i ++) {
			attributeNames[i] = RandomUtils.getString(1, 10);
			ValueType type = RandomUtils.getFrec(typeFrequencies);
			attributeValues[i] = AttributeGenerator.generateValues(type, valuesCount.get(type));
			indexes.put(attributeNames[i], type);
		}
		
		simpleEngineConfig = new EngineConfig(indexes);
	}

	@Override
	public EngineConfig getEngineConfig() {
		return simpleEngineConfig;
	}

	@Override
	public DocumentGeneratorBase getDocumentGenerator() {
		return new DocumentGeneratorBase(false, documentsCount) {

			@Override
			protected Map<String, List<Value>> generateDocument() {
				Map<String, List<Value>> document = new HashMap<>(attributesCount);
				for(int i = 0; i < attributesCount; i ++) {
					Value val = RandomUtils.gaussianRandomElement(attributeValues[i]);
					document.put(attributeNames[i], Arrays.asList(val));
				}
				return document;
			}
		};
	}

	@Override
	public RequestGeneratorBase getRequestGenerator() {
		return new RequestGeneratorBase(false, requestsCount) {
			@Override
			protected Request generateRequest() {
				Request request = Request.build();
				Set<Integer> attributeIndexes = RandomUtils.getRandomIndexes(attributesCount, reqAttributesCount);
				for(int idx: attributeIndexes) {
					Value val = RandomUtils.gaussianRandomElement(attributeValues[idx]);
					request.exact(attributeNames[idx], val.getValue());
				}
				return request;
			}
		};
	}

	@Override
	public String toString() {
		return String.format("MultyAttributesTest [%s attributes, %s documents, %s requests, %s requested attributes]", 
				attributesCount, documentsCount, requestsCount, reqAttributesCount);
	}

}
