package ru.csc.vindur.test.testHelpers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.csc.vindur.EngineConfig;
import ru.csc.vindur.Request;
import ru.csc.vindur.Value;
import ru.csc.vindur.ValueType;
import ru.csc.vindur.test.DocumentGeneratorBase;
import ru.csc.vindur.test.RequestGeneratorBase;
import ru.csc.vindur.test.utils.AttributeGenerator;
import ru.csc.vindur.test.utils.RandomUtils;

public class OneAttributeTest implements TestHelper {
	private static final int EXPECTED_VOLUME = 1000000;
	private static final int REQUESTS_COUNT = 100;
	private final Value[] attributeValues;
	private final EngineConfig simpleEngineConfig;
	
	public OneAttributeTest(ValueType valueType, int valuesCount) {
		Map<String, ValueType> indexes = new HashMap<>(1);
		indexes.put("attribute", valueType);
		switch (valueType) {
		case ENUM:
			attributeValues = AttributeGenerator.generateStringValues(valuesCount, 1, 10);
			break;
		case NUMERIC:
			attributeValues = AttributeGenerator.generateNumericValues(valuesCount, 1, 10);
			break;
		case STRING:
			attributeValues = AttributeGenerator.generateStringValues(valuesCount, 1, 10);
			break;
		default:
			throw new RuntimeException("Missing case state");	
		}
		simpleEngineConfig = new EngineConfig(indexes, EXPECTED_VOLUME);
	}


	@Override
	public DocumentGeneratorBase getDocumentGenerator() {
		return new DocumentGeneratorBase(false, EXPECTED_VOLUME) {
			@Override
			protected Map<String, List<Value>> generateDocument() {
				Map<String, List<Value>> document = new HashMap<>(1);
				Value val = RandomUtils.gaussianRandomElement(attributeValues);
				document.put("attribute", Arrays.asList(val));
				return document;
			}
		};
	}

	@Override
	public RequestGeneratorBase getRequestGenerator() {
		return new RequestGeneratorBase(false, REQUESTS_COUNT) {
			@Override
			protected Request generateRequest() {
				Value val = RandomUtils.gaussianRandomElement(attributeValues);
				Request request = Request.build().exact("attribute", val.getValue());
				return request;
			}
		};
	}

	@Override
	public EngineConfig getEngineConfig() {
		return simpleEngineConfig;
	}

}
