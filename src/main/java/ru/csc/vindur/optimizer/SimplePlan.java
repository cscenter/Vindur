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
        if (current >= steps.size()) {
            return null;
        }
        return steps.get(current++);
    }

    @Override
    public List<Step> cutTail() {
        List<Step> tail = steps.subList(this.current, steps.size());
        steps.removeAll(tail);
        return tail;
    }

    @Override
    public void addStep(Step step) {
        steps.add(step);
    }
}
