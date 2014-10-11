package ru.csc.vindur;

import ru.csc.vindur.physics.INumericsConverter;

/**
 * Created by Pavel Chursin on 05.10.2014.
 */
public class IndexDefinition {
    private ValueType valueType;
    private INumericsConverter converter;

    public IndexDefinition(ValueType valueType) {
        this(valueType, null);
    }

    public IndexDefinition(ValueType valueType, INumericsConverter converter) {
        this.valueType = valueType;
        this.converter = converter;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public INumericsConverter getConverter() {
        return converter;
    }
}
