package ru.csc.vindur.storage;

public abstract class StorageRangeBase<T extends Comparable<T>> extends StorageBase<T, RangeRequest> {

    public StorageRangeBase(Class<T> type)
    {
    	super(type, RangeRequest.class);
    }

	@SuppressWarnings("unchecked")
	@Override
	public boolean checkValue(int docId, T value, RangeRequest request) {
		return value.compareTo((T) request.getLowBound()) >= 0 && value.compareTo((T) request.getUpperBound()) <= 0;
	}
	
	@Override
	public boolean validateRequestType(Object request) {
		if(!super.validateRequestType(request)) {
			return false;
		}
		return validateValueType(((RangeRequest)request).getLowBound()) && validateValueType(((RangeRequest)request).getUpperBound());
	}

	public static RangeRequest generateRequest(Object lowBound, Object upperBound)  {
		return new RangeRequest(lowBound, upperBound);
	}
}