package ru.csc.vindur.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ru.csc.vindur.Request;

public abstract class RequestGeneratorBase implements Iterable<Request> {

	private static final String GET_SAVED_UNSUPPORTED = "Generator was created without saveRequests option enabled";
	private final boolean saveRequests;
	private final int requestsCount;
	private final List<Request> requestsList;

	public RequestGeneratorBase(boolean saveRequests, int requestsCount) {
		this.saveRequests = saveRequests;
		this.requestsCount = requestsCount;
		if(saveRequests) {
			requestsList = new ArrayList<>(requestsCount);
		} else {
			requestsList = null;
		}
	}
	
	protected abstract Request generateRequest();
	
	/**
	 * If there was no iterating over this object entities will be created here
	 * @throws UnsupportedOperationException if saveEntities option is not enabled
	 * @return saved Entities
	 */
	public List<Request> getSavedRequests() {
		if(!saveRequests) {
			throw(new UnsupportedOperationException(GET_SAVED_UNSUPPORTED));
		}
		if(requestsList.size() < requestsCount) {
			for(int i = requestsList.size(); i < requestsCount; i ++) {
				requestsList.add(generateRequest());
			}
		}
		return Collections.unmodifiableList(requestsList);
	}

	@Override
	public Iterator<Request> iterator() {
		return new Iterator<Request>() {
			private int requestsReturned = 0;
			
			@Override
			public Request next() {
				if(!hasNext()) {
					throw(new IllegalStateException("getting next from itterator when it hasn't next"));
				}
				requestsReturned ++;
				if(saveRequests) {
					if(requestsReturned <= requestsList.size()) {
						return requestsList.get(requestsReturned - 1);
					}
					Request request = generateRequest();
					requestsList.add(request);
					return request;
				}
				return generateRequest();
			}
			
			@Override
			public boolean hasNext() {
				return requestsReturned < requestsCount;
			}
		};
	}

	public int getRequestsCount() {
		return requestsCount;
	}
}
