package ru.csc.vindur.optimizer;

import ru.csc.vindur.Request;
import ru.csc.vindur.bitset.BitSet;

import java.awt.font.NumericShaper;
import java.util.List;

/**
 * Created by Edgar on 03.11.14.
 */
public class Step {
    private String storageName;
    private String from;
    private String to;
    private Type type;

    List<Step> stepList;

    public Step(String storageName, String from, String to, Type type) {
        this.storageName = storageName;
        this.from = from;
        this.to = to;
        this.type = type;
    }

    public Step(List<Step> stepList)
    {
        this.stepList = stepList;
        this.type = Type.DIRECT;
    }

    public List<Step> getStepList() throws UnsupportedOperationException
    {
        if (this.type == Type.DIRECT)
        {
            return stepList;
        }
        else
        {
            throw new UnsupportedOperationException();
        }
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
        RANGE,
        DIRECT
    }
}
