package ru.csc.vindur.test.testHelper;

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

public class TestHelper {
	private static final int EXPECTED_VOLUME = 100000;
	private static final int REQUESTS_COUNT = 10000;
	private final EngineConfig simpleEngineConfig;
	private final Map<String, List<Value>> document;
	private final Request request;
	
	public TestHelper() {
		Map<String, ValueType> indexes = new HashMap<>(1);
		indexes.put("attribute", ValueType.STRING);
		simpleEngineConfig = new EngineConfig(indexes, EXPECTED_VOLUME);
		document = new HashMap<>(1);
		document.put("attribute", Arrays.asList(new Value("value")));
		request = Request.build().exact("attribute", "value");
	}

	public EngineConfig getEngineConfig() {
		return simpleEngineConfig;
	}

	public DocumentGeneratorBase getDocumentGenerator() {
		return new DocumentGeneratorBase(false, EXPECTED_VOLUME) {
			@Override
			protected Map<String, List<Value>> generateDocument() {
				return document;
			}
		};
	}

	public RequestGeneratorBase getRequestGenerator() {
		return new RequestGeneratorBase(false, REQUESTS_COUNT) {
			@Override
			protected Request generateRequest() {
				return request;
			}
		};
	}

}
