package ru.csc.vindur.document;

/**
 * @author Andrey Kokorev
 * Created on 24.09.2014.
 */
public class Value {
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
