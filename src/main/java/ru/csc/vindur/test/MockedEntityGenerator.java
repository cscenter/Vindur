package ru.csc.vindur.test;

import ru.csc.vindur.entity.Entity;


public class MockedEntityGenerator extends EntityGeneratorBase {

	public MockedEntityGenerator(boolean saveEntities_, int entitiesCount_) {
		super(saveEntities_, entitiesCount_);
	}

	private Entity entity = new Entity("Mocked entity id");

	@Override
	protected Entity generateEntity() {
		return entity;
	}

}
