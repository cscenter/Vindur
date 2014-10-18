package ru.csc.vindur.test.utils;

import ru.csc.vindur.Value;

public class AttributeGenerator {

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
