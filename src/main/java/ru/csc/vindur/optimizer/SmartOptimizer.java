package ru.csc.vindur.optimizer;

import ru.csc.vindur.Engine;
import ru.csc.vindur.Request;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Edgar_Work on 11.11.2014.
 */

public class SmartOptimizer implements Optimizer
{
    private long threshold;

    public SmartOptimizer(long threshold)
    {
        this.threshold = threshold;
    }

    @Override
    public Plan generatePlan(Request request, Engine engine)
    {
        List<Request.RequestPart> parts = request.getRequestParts().stream().collect(Collectors.toList());

        for (Request.RequestPart requestPart : parts)
        {
            if (engine.getStorage(requestPart.getTag()) == null)
            {
                //todo: кинуть исключение? создать дефолтный Storage?
            }

        }

        parts.sort((a, b) -> Long.compare(engine.getStorage(a.getTag()).size() * engine.getStorage(a.getTag()).getComplexity(), (engine.getStorage(b.getTag()).size() * engine.getStorage(b.getTag()).getComplexity())));


        Plan plan = new Plan();

        for (Request.RequestPart requestPart : parts)
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
        if (currentResultSize < this.threshold) //todo эту константу надо бы куда-нибудь наружу выпихнуть. В конструктор?
        {
            List<Step> tail = plan.cutTail();
            Step directStep = new Step(tail);
            plan.addStep(directStep);
        }
    }
}
