package ru.csc.vindur.storage;

import ru.csc.vindur.bitset.ROBitArray;

public abstract class Storage<V, R> {

    private final Class<V> valueClazz;
    private final Class<R> requestClazz;
    private int documentsCount = 0;

    public abstract void add(int docId, V value);

    public abstract ROBitArray findSet(R request);

    public abstract boolean checkValue(int docId, V value, R request);

    public Storage(Class<V> valueClazz, Class<R> requestClazz) {
        this.valueClazz = valueClazz;
        this.requestClazz = requestClazz;
    }

    /**
     * @param value
     * @return true if <code>value instanceof V</code>
     */
    public boolean validateValueType(Object value) {
        return valueClazz.isAssignableFrom(value.getClass());
    }

    /**
     * @param request
     * @return true if <code>query instanceof R</code>
     */
    public boolean validateRequestType(Object request) {
        return requestClazz.isAssignableFrom(request.getClass());
    }

    public int documentsCount() {
        return documentsCount;
    }

    public int getComplexity() {
        return 0;
    }

    protected void incrementDocumentsCount() {
        documentsCount += 1;
    }

}
