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
    @Override
    public Plan generatePlan(Request request, Engine engine)
    {
        List<Request.RequestPart> lr = request.getRequestParts().stream().collect(Collectors.toList());
        //todo: check if storage exists

        lr.sort((a, b) -> Long.compare(engine.getStorage(a.getTag()).size(), (engine.getStorage(b.getTag()).size())));


        Plan plan = new Plan();

        for (Request.RequestPart requestPart : lr) {
            Step step = new Step(requestPart.getTag(), requestPart.getFrom(), requestPart.getFrom(), Step.Type.EXACT);
            plan.addStep(step);
        }

        return plan;
    }


    @Override
    public void updatePlan(Plan plan, int currentResultSize)
    {
        if (currentResultSize < 5000) //todo эту константу надо бы куда-нибудь наружу выпихнуть. В конструктор?
        {
            List<Step> tail = plan.cutTail();
            Step directStep = new Step(tail);
            plan.addStep(directStep);
        }
    }
}
