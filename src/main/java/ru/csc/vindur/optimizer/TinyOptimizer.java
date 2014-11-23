package ru.csc.vindur.optimizer;

import java.util.ArrayList;
import java.util.List;

import ru.csc.vindur.Engine;
import ru.csc.vindur.Request;

/**
 * Created by Edgar on 26.10.14.
 */

public class TinyOptimizer implements Optimizer
{
    public TinyOptimizer()
    {
    }

    @Override
    public Plan generatePlan(Request request, Engine engine)
    {
        /*for each request part get index, if index is not null, get expectedAmount() else do full scan
         *(or set expectedAmount to very big constant), then sort by expectedAmount() and return such plan
         */
        List<Request.RequestPart> lr = new ArrayList<>();
        for (Request.RequestPart requestPart : request.getRequestParts())
        {
            //todo: check if storage exists
            lr.add(requestPart);
        }


        lr.sort((a, b) -> Long.compare(engine.getStorage(a.getTag()).size(), (engine.getStorage(b.getTag()).size())));


        Plan plan = new Plan();

        for (Request.RequestPart requestPart : lr)
        {
            Step step;
            if (requestPart.isExact())
            {
                step = new Step(requestPart.getTag(), requestPart.getFrom(), requestPart.getFrom(), Step.Type.EXACT);
            } else
            {
                step = new Step(requestPart.getTag(), requestPart.getFrom(), requestPart.getTo(), Step.Type.RANGE);
            }
            plan.addStep(step);
        }

        return plan;
    }

    @Override
    public void updatePlan(Plan plan, int currentResultSize)
    {

    }
}
