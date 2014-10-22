package ru.csc.vindur.test.utils;

import ru.csc.vindur.document.Value;
import ru.csc.vindur.document.ValueType;

public class AttributeGenerator {

	public static Value[] generateValues(ValueType valueType, int valuesCount) {
		Value[] values;
		switch (valueType) {
		case ENUM:
			values = generateStringValues(valuesCount, 1, 10);
			break;
		case NUMERIC:
			values = generateNumericValues(valuesCount, 1, 10);
			break;
		case STRING:
			values = generateStringValues(valuesCount, 1, 10);
			break;
		default:
			throw new RuntimeException("Missing case state");	
		}
		return values;
	}
	
	public static Value[] generateStringValues(int valuesCount, int minLen, int maxLen) {
		Value[] values = new Value[valuesCount];
		for(int i = 0; i < valuesCount; i ++) {
			values[i] = new Value(RandomUtils.getString(minLen, maxLen));
		}
		return values;
	}

	public static Value[] generateNumericValues(int valuesCount, int minLen, int maxLen) {
		Value[] values = new Value[valuesCount];
		for(int i = 0; i < valuesCount; i ++) {
			values[i] = new Value(RandomUtils.getNumericString(minLen, maxLen));
		}
		return values;
	}

}
