package ru.csc.vindur.executor;

import java.util.List;

/**
 * Created by Edgar on 26.10.14.
 */
public interface Plan {
    public Step next();

    public List<Step> cutTail();

    public void addStep(Step step);
}
