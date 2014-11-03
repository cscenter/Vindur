package ru.csc.vindur.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class GeneratorBase<T> implements Iterable<T> {

	private static final String GET_SAVED_UNSUPPORTED = "Generator was created without saveEntities option enabled";
	private final boolean saveEntities;
	private final int entitiesCount;
	private final List<T> savedEntities;

	/**
	 * 
	 * @param saveEntities if true generated entities will be saved into a list
	 * @param entitiesCount entities count to be generated
	 */
	public GeneratorBase(boolean saveEntities, int entitiesCount) {
		this.saveEntities = saveEntities;
		this.entitiesCount = entitiesCount;
		if(saveEntities) {
			savedEntities = new ArrayList<>(entitiesCount);
		} else {
			savedEntities = null;
		}
	}
	
	protected abstract T generateEntity();
	
	/**
	 * If there was no iterating over this object entities will be created here
	 * @throws UnsupportedOperationException if saveEntities option is not enabled
	 * @return saved Entities
	 */
	public List<T> getSavedEntities() {
		if(!saveEntities) {
			throw(new UnsupportedOperationException(GET_SAVED_UNSUPPORTED));
		}
		if(savedEntities.size() < entitiesCount) {
			for(int i = savedEntities.size(); i < entitiesCount; i ++) {
				savedEntities.add(generateEntity());
			}
		}
		return Collections.unmodifiableList(savedEntities);
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private int entitiesReturned = 0;
			
			@Override
			public T next() {
				if(!hasNext()) {
					throw(new IllegalStateException("getting next from itterator when it hasn't next"));
				}
				entitiesReturned ++;
				if(saveEntities) {
					if(entitiesReturned <= savedEntities.size()) {
						return savedEntities.get(entitiesReturned - 1);
					}
					T entity = generateEntity();
					savedEntities.add(entity);
					return entity;
				}
				return generateEntity();
			}
			
			@Override
			public boolean hasNext() {
				return entitiesReturned < entitiesCount;
			}
		};
	}

	public int getEntitiesCount() {
		return entitiesCount;
	}
}
