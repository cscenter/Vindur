package ru.csc.vindur.storage;

import ru.csc.vindur.bitset.ROBitSet;

/**
 * @param <V> Stored value type
 * @param <R> Request type
 */
public interface Storage<V, R> {
	// TODO find out the better way to check types
	// default methods can't be used
    public void add(int docId, V value);
    public ROBitSet findSet(R request);
    public boolean checkValue(V value, R request);
    public int documentsCount();
    public int getComplexity();
    /**
     * @param value
     * @return true if <code>value instanceof V</code>
     */
    public boolean validateValueType(Object value);

    /**
     * @param request
     * @return true if <code>request instanceof R</code>
     */
    public boolean validateRequestType(Object request);
}
