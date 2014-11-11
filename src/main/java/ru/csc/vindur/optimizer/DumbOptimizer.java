package ru.csc.vindur.optimizer;

import ru.csc.vindur.Engine;
import ru.csc.vindur.Request;


/**
 * Created by Edgar_Work on 11.11.2014.
 */
public class DumbOptimizer implements Optimizer
{
    @Override
    public Plan generatePlan(Request request, Engine engine)
    {
        Plan plan = new Plan();
        for (Request.RequestPart requestPart : request.getRequestParts())
        {
            //todo: check if storage exists
            if (requestPart.isExact())
            {
                plan.addStep(new Step(requestPart.getTag(), requestPart.getFrom(), requestPart.getFrom(), Step.Type.EXACT));
            } else
            {
                plan.addStep(new Step(requestPart.getTag(), requestPart.getFrom(), requestPart.getFrom(), Step.Type.RANGE));
            }
        }
        return plan;
    }

    @Override
    public void updatePlan(Plan plan, int currentResultSize)
    {

    }
}
