package ru.csc.vindur.optimizer;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Edgar on 26.10.14.
 */
public class Plan {
    List<Step> steps;
    AtomicInteger currentResultSize;

    public Plan() {
        steps = new LinkedList<>();
    }

    public void addStep(Step step) {
        steps.add(step);
    }

    public List<Step> getSteps() {
        return steps;
    }

    public AtomicInteger getCurrentResultSize() {
        return currentResultSize;
    }

    public void setCurrentResultSize(AtomicInteger currentResultSize) {
        this.currentResultSize = currentResultSize;
    }
}
