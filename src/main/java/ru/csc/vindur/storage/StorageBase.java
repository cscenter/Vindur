package ru.csc.vindur.storage;

public abstract class StorageBase<V, R> implements Storage<V, R> {

	private final Class<V> valueClazz;
	private final Class<R> requestClazz;
	private int documentsCount = 0;

	public StorageBase(Class<V> valueClazz, Class<R> requestClazz) {
		this.valueClazz = valueClazz;
		this.requestClazz = requestClazz;
	}
	
	@Override
	public boolean validateValueType(Object value) {
		return valueClazz.isAssignableFrom(value.getClass());
	}

	@Override
	public boolean validateRequestType(Object request) {
		return requestClazz.isAssignableFrom(request.getClass());
	}
	@Override
	public int documentsCount() {
		return documentsCount;
	}

	@Override
	public int getComplexity()	{ return 0;	}

	protected void incrementDocumentsCount() {
		documentsCount += 1;
	}

}
