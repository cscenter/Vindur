package ru.csc.vindur.storage;

public class RangeRequest {
    private final Object lowBound;
    private final Object upperBound;

    public RangeRequest(Object lowBound, Object upperBound) {
        this.lowBound = lowBound;
        this.upperBound = upperBound;
    }

    public Object getLowBound() {
        return lowBound;
    }

    public Object getUpperBound() {
        return upperBound;
    }

    @Override
    public String toString() {
        return "[" +
                lowBound +
                " -> " + upperBound +
                ']';
    }
}