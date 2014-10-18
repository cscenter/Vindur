package ru.csc.vindur.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ru.csc.vindur.entity.Entity;

public abstract class EntityGeneratorBase implements Iterable<Entity> {

	private static final String GET_SAVED_UNSUPPORTED = "Generator was created without saveEntities option enabled";
	private boolean saveEntities;
	private int entitiesCount;
	private List<Entity> entitiesList;

	/**
	 * 
	 * @param saveEntities if true generated entities will be saved into a list
	 * @param entitiesCount entities count to be generated
	 */
	public EntityGeneratorBase(boolean saveEntities_, int entitiesCount_) {
		saveEntities = saveEntities_;
		entitiesCount = entitiesCount_;
		if(saveEntities) {
			entitiesList = new ArrayList<>(entitiesCount);
		} else {
			entitiesList = null;
		}
	}
	
	protected abstract Entity generateEntity();
	
	/**
	 * If there was no iterating over this object entities will be created here
	 * @throws UnsupportedOperationException if saveEntities option is not enabled
	 * @return saved Entities
	 */
	public List<Entity> getSavedEntities() {
		if(!saveEntities) {
			throw(new UnsupportedOperationException(GET_SAVED_UNSUPPORTED));
		}
		if(entitiesList.size() < entitiesCount) {
			for(int i = entitiesList.size(); i < entitiesCount; i ++) {
				entitiesList.add(generateEntity());
			}
		}
		return Collections.unmodifiableList(entitiesList);
	}

	@Override
	public Iterator<Entity> iterator() {
		return new Iterator<Entity>() {
			private int entitiesReturned = 0;
			
			@Override
			public Entity next() {
				if(!hasNext()) {
					throw(new IllegalStateException("getting next from itterator when it hasn't next"));
				}
				entitiesReturned ++;
				if(saveEntities) {
					if(entitiesReturned <= entitiesList.size()) {
						return entitiesList.get(entitiesReturned - 1);
					}
					Entity entity = generateEntity();
					entitiesList.add(entity);
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
}
