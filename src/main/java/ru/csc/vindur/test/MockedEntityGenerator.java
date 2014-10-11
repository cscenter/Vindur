package ru.csc.vindur.test;

import java.util.Iterator;

import ru.csc.njord.entity.Entity;

public class MockedEntityGenerator extends EntityGeneratorBase {

	private Entity entity = new Entity("Mocked entity id");

	@Override
	public Iterator<Entity> iterator() {
		return new Iterator<Entity>() {
			private boolean hasNext = true;
			
			@Override
			public boolean hasNext() {
				return hasNext;
			}

			@Override
			public Entity next() {
				hasNext = false;
				return entity ;
			}
		};
	}

}
