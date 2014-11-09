package ru.csc.vindur.optimizer;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Edgar on 26.10.14.
 */
public class Plan {
    List<Step> steps;

    public Plan() {
        steps = new LinkedList<>();
    }

    public void addStep(Step step) {
        steps.add(step);
    }

    public List<Step> getSteps() {
        return steps;
    }
}
