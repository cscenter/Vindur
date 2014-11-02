package ru.csc.vindur.test.testHelpers;

import java.util.HashMap;
import java.util.Map;

import ru.csc.vindur.EngineConfig;
import ru.csc.vindur.bitset.bitsetFabric.BitSetFabric;
import ru.csc.vindur.document.StorageType;
import ru.csc.vindur.test.DocumentGeneratorBase;
import ru.csc.vindur.test.RequestGeneratorBase;

public class OneAttributeTest implements TestHelper {
	private final MultyAttributesTest testHelper;
	private final String descriptionStr; 
	
	public OneAttributeTest(StorageType valueType, int valuesCount, int documentsCount, int requestsCount, BitSetFabric bitSetFabric) {
		Map<StorageType, Double> typeFrequencies = new HashMap<>(1);
		typeFrequencies.put(valueType, 1.0);
		Map<StorageType, Integer> valuesCountMap = new HashMap<>(1);
		valuesCountMap.put(valueType, valuesCount);
		testHelper = new MultyAttributesTest(1, typeFrequencies, valuesCountMap, 
				documentsCount, requestsCount, 1, bitSetFabric);
		descriptionStr = String.format("OneAttributeTest [%s value type, %s values, %s documents, %s requests]", 
				valueType, valuesCount, documentsCount, requestsCount);
	}


	@Override
	public DocumentGeneratorBase getDocumentGenerator() {
		return testHelper.getDocumentGenerator();
	}

	@Override
	public RequestGeneratorBase getRequestGenerator() {
		return testHelper.getRequestGenerator();
	}

	@Override
	public EngineConfig getEngineConfig() {
		return testHelper.getEngineConfig();
	}


	@Override
	public String toString() {
		return descriptionStr;
	}

}
