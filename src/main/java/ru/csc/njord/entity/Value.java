package ru.csc.njord.entity;

/**
 * @author Andrey Kokorev
 * Created on 24.09.2014.
 */
public class Value {
    public static final int MAX_WEIGHT = 100;

    private String value;

    public Value(String val) {
        value = val;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue();
    }
}
