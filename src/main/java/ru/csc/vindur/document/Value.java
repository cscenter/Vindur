package ru.csc.vindur.document;

/**
 * @author Andrey Kokorev
 * Created on 24.09.2014.
 */
public class Value {
    private final String value;
    private final Integer parentId;

    public Value(String value) {
        this(value, null);
    }

    public Value(String value, Integer parentId) {
        this.value = value;
        this.parentId = parentId;
    }

    public String getValue() {
        return value;
    }

    public Integer getParentId() {
        return parentId;
    }

    @Override
    public String toString() {
        return value;
    }
}
