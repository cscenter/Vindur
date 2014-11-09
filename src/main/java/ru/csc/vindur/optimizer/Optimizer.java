package ru.csc.vindur.optimizer;

import ru.csc.vindur.Request;

/**
 * Created by Edgar on 26.10.14.
 */
public interface Optimizer
{
    public Plan generatePlan(Request request);
    public void updatePlan(Plan plan);
}
