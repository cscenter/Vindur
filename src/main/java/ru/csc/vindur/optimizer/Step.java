package ru.csc.vindur.optimizer;

import ru.csc.vindur.Request;
import ru.csc.vindur.bitset.BitSet;

import java.awt.font.NumericShaper;

/**
 * Created by Edgar on 03.11.14.
 */
public class Step {
    private String storageName;
    private String from;
    private String to;
    private Type type;

    public Step(String storageName, String from, String to, Type type) {
        this.storageName = storageName;
        this.from = from;
        this.to = to;
        this.type = type;
    }

    public String getStorageName() {
        return storageName;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Type getType() {
        return type;
    }


    public static enum Type {
        EXACT,
        RANGE;
    }
}
