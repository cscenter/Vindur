package ru.csc.vindur.optimizer;

import ru.csc.vindur.Engine;
import ru.csc.vindur.Request;
import ru.csc.vindur.bitset.BitSet;

import java.util.function.Supplier;

/**
 * Created by Edgar on 26.10.14.
 */
public interface Optimizer
{
    /**
     * @param request
     * @param engine
     * @return
     */
    public Plan generatePlan(Request request, Engine engine);

    public void updatePlan(Plan plan, int currentResultSize);
}
