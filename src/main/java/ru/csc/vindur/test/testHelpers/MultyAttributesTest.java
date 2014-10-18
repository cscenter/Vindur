package ru.csc.vindur.test.testHelpers;

import java.util.Map;

import ru.csc.vindur.EngineConfig;
import ru.csc.vindur.ValueType;
import ru.csc.vindur.test.DocumentGeneratorBase;
import ru.csc.vindur.test.RequestGeneratorBase;

public class MultyAttributesTest implements TestHelper {
	
	public MultyAttributesTest(int attributesCount, Map<ValueType, Double> typeFrequencies, 
			Map<ValueType, Integer> valuesCount, int documentsCount, int requestsCount) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public EngineConfig getEngineConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DocumentGeneratorBase getDocumentGenerator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestGeneratorBase getRequestGenerator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		return String.format("MultyAttributesTest []");
	}

}
