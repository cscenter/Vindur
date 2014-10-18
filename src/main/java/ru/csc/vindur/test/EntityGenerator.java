package ru.csc.vindur.test;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Map;

import ru.csc.vindur.entity.Entity;
import ru.csc.vindur.entity.Value;

public class EntityGenerator extends EntityGeneratorBase {
	
	private EntitiesDescriptor entitiesDescriptor;
	long id = 0L;

	public EntityGenerator(boolean saveEntities, int entitiesCount, EntitiesDescriptor entitiesDescriptor_) {
		super(saveEntities, entitiesCount);
		entitiesDescriptor = entitiesDescriptor_;
		
	}
	
	@Override
	protected Entity generateEntity() {
		int aspectsCount = entitiesDescriptor.getAspectsCount();
		// TODO change aspects choosing
		BitSet choosenAspects = RandomUtils.getBitSet(aspectsCount, aspectsCount / 2);
		// TODO find out correct id's
		Entity result = new Entity(String.valueOf(id));
		id ++;
		Map<String, ArrayList<Value>> values = result.getValues();
		for (int i = choosenAspects.nextSetBit(0); i >= 0; i = choosenAspects.nextSetBit(i+1)) {
			AspectDescriptor aspectDescriptor = entitiesDescriptor.getAspectDescriptor(i);
			String aspectName = aspectDescriptor.getName();
			ArrayList<Value> aspectValues = aspectDescriptor.generateAspectValues();
			values.put(aspectName, aspectValues);
		}
		return result;
	}
	
}
