package ru.csc.vindur.optimizer;

import java.util.List;

public class SimplePlan implements Plan {
	private int current = 0;
	private final List<Step> steps;
	
	public SimplePlan(List<Step> steps) {
		this.steps = steps;
	}

	@Override
	public Step next() {
		if(current >= steps.size()) {
			return null;
		}
		return steps.get(current++);
	}
}
