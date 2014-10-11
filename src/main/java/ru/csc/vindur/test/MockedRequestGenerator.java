package ru.csc.vindur.test;

import java.util.Iterator;

import ru.csc.vindur.Request;

public class MockedRequestGenerator extends RequestGeneratorBase {
	private Request request = Request.build();
	
	@Override
	public Iterator<Request> iterator() {
		return new Iterator<Request>() {
			private boolean hasNext = true;
			
			@Override
			public boolean hasNext() {
				return hasNext;
			}

			@Override
			public Request next() {
				hasNext = false;
				return request ;
			}
		};
	}

}
