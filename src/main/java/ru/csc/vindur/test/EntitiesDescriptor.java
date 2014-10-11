package ru.csc.vindur.test;

import java.util.ArrayList;
import java.util.List;

public class EntitiesDescriptor {

	private List<AspectDescriptor> aspectDescriptors = new ArrayList<AspectDescriptor>();
	
	public List<AspectDescriptor> getAspectDescriptors() {
		return aspectDescriptors;
	}
	
	public int getAspectsCount() {
		return aspectDescriptors.size();
	}

	public AspectDescriptor getAspectDescriptor(int index) {
		return aspectDescriptors.get(index);
	}

}
