package ru.csc.vindur.test;

import java.util.ArrayList;

import ru.csc.vindur.IIndex;
import ru.csc.vindur.entity.Value;

public abstract class AspectDescriptor {
	
	private String name;
	private IIndex index;

	public AspectDescriptor(String name_, IIndex index_) {
		name = name_;
		index = index_;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	
	public IIndex getIndex() {
		// TODO Auto-generated method stub
		return index;
	}

	public abstract ArrayList<Value> generateAspectValues();

}
