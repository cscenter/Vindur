package ru.csc.vindur.test;

import java.util.ArrayList;

import ru.csc.vindur.entity.Value;

public abstract class AspectDescriptor {
	
	private String name;

	public AspectDescriptor(String name_) {
		name = name_;
	}

	public String getName() {
		return name;
	}

	public abstract ArrayList<Value> generateAspectValues();

}
