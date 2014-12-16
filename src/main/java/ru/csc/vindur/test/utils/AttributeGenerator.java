package ru.csc.vindur.test.utils;

import ru.csc.vindur.storage.StorageType;

public class AttributeGenerator {

    public static Object[] generateValues(StorageType valueType, int valuesCount) {
        Object[] values;
        switch (valueType) {
        case INTEGER:
        case RANGE_INTEGER:
            values = generateNumericValues(valuesCount, 0, 10000000);
            break;
        case STRING:
        case RANGE_STRING:
            values = generateStringValues(valuesCount, 1, 10);
            break;
        case LUCENE_STRING:
            values = generateStringValues(valuesCount, 1, 1000);
            break;
        default:
            throw new RuntimeException("Missing case state");
        }
        return values;
    }

    public static Object[] generateStringValues(int valuesCount, int minLen,
            int maxLen) {
        Object[] values = new Object[valuesCount];
        for (int i = 0; i < valuesCount; i++) {
            values[i] = RandomUtils.getString(minLen, maxLen).toLowerCase();
        }
        return values;
    }

    public static Object[] generateNumericValues(int valuesCount, int min,
            int max) {
        Object[] values = new Object[valuesCount];
        for (int i = 0; i < valuesCount; i++) {
            values[i] = RandomUtils.getNumber(min, max);
        }
        return values;
    }

}
