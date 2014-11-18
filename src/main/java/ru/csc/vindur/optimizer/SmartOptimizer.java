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
    //todo: здесь уже придется знать список частей запроса и размер текущего результата?
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


    //todo: а может возвращать лямбду, каким-нибудь образом?
    //todo: то есть если результ небольшой - вернуть лямбду, которая бы проверяла все вручную?
    //todo: а может научить движок проверять все вручную? - научил
    @Override
    public void updatePlan(Plan plan, int currentResultSize)
    {
        if (currentResultSize < 5000)
        {
            List<Step> tail = plan.cutTail();
            Step directStep = new Step(tail);
            plan.addStep(directStep);
        }
        // todo: здесь как-то надо понять, что результат небеольшой, остановиться и проверять вручную? Или проапдейтить размер результата?
    }
}
