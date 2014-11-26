package ru.csc.vindur.optimizer;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Edgar on 26.10.14.
 */
public class Plan
{
    private List<Step> steps;
    private Integer currentStep = 0;

    public Plan()
    {
        steps = new LinkedList<>();
    }

    public void addStep(Step step)
    {
        steps.add(step);
    }

    public List<Step> cutTail()
    {
        return steps.subList(currentStep, steps.size());
    }

    public Step next()
    {
        if (currentStep >= steps.size()) return null;
        return steps.get(currentStep++);
    }
}
