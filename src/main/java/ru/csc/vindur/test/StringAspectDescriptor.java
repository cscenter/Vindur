package ru.csc.vindur.test;

import java.util.ArrayList;

import ru.csc.vindur.entity.Value;

public class StringAspectDescriptor extends AspectDescriptor {

	private int minLen;
	private int maxLen;

	public StringAspectDescriptor(String name_, int minLen_, int maxLen_) {
		super(name_);
		minLen = minLen_;
		maxLen = maxLen_;
	}
	
	@Override
	public ArrayList<Value> generateAspectValues() {
		ArrayList<Value> result = new ArrayList<Value>();
		result.add(new Value(RandomUtils.getString(maxLen, minLen)));
		return result;
	}
}
