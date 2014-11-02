package ru.csc.vindur.test;

import ru.csc.vindur.document.Value;
import ru.csc.vindur.document.StorageType;

/**
 * Created by Pavel Chursin on 28.10.2014.
 */
public abstract class AttributeDescriptor {
    private String attributeName;
    private StorageType valueType;

    public AttributeDescriptor(String attributeName, StorageType valueType) {
        this.attributeName = attributeName;
        this.valueType = valueType;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public StorageType getValueType() {
        return valueType;
    }

    public abstract Value generateValue(Object ...params);
}
