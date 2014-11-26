package ru.csc.vindur.test.utils;

import ru.csc.vindur.storage.StorageType;

public class AttributeGenerator
{

    public static Object[] generateValues(StorageType valueType, int valuesCount)
    {
    	Object[] values;
        switch (valueType)
        {
            case NUMERIC:
                values = generateNumericValues(valuesCount, 0, Integer.MAX_VALUE);
                break;
            case STRING:
                values = generateStringValues(valuesCount, 1, 10);
                break;
            default:
                throw new RuntimeException("Missing case state");
        }
        return values;
    }

    public static Object[] generateStringValues(int valuesCount, int minLen, int maxLen)
    {
    	Object[] values = new Object[valuesCount];
        for (int i = 0; i < valuesCount; i++)
        {
            values[i] = RandomUtils.getString(minLen, maxLen);
        }
        return values;
    }

    public static Object[] generateNumericValues(int valuesCount, int min, int max)
    {
    	Object[] values = new Object[valuesCount];
        for (int i = 0; i < valuesCount; i++)
        {
            values[i] = RandomUtils.getNumber(min, max);
        }
        return values;
    }

}
