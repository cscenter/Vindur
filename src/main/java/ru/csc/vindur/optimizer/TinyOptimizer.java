package ru.csc.vindur.optimizer;

import ru.csc.vindur.Engine;
import ru.csc.vindur.EngineConfig;
import ru.csc.vindur.Request;
import ru.csc.vindur.storage.Storage;

import java.util.*;

/**
 * Created by Edgar on 26.10.14.
 */

public class TinyOptimizer implements Optimizer {
    public TinyOptimizer(Map<String, Storage> indexes) {

    }

    @Override
    public Plan generatePlan(Request request, Engine engine) {
        /*for each request part get index, if index is not null, get expectedAmount() else do full scan
         *(or set expectedAmount to very big constant), then sort by expectedAmount() and return such plan
         */
        List<Request.RequestPart> lr = new ArrayList<>();
        for (Request.RequestPart requestPart : request.getRequestParts()) {
            Storage index = engine.getStorage(requestPart.getTag());
            lr.add(requestPart);
        }


        lr.sort((a, b) -> engine.getStorage(a.getTag()).getSize() > engine.getStorage(b.getTag()).getSize());


        Plan plan = new Plan();

        for (Request.RequestPart requestPart : lr) {
            //Step step = entry.getValue() < 5000 ? new FilterStep(entry.getKey(), plan, engineConfig) : new PatternStep(entry.getKey(), plan, engineConfig);
            Step step = new Step(requestPart.getTag(), requestPart.getFrom(), requestPart.getFrom(), Step.Type.EXACT);
            plan.addStep(step);
        }

        return plan;
    }

    @Override
    public void updatePlan(Plan plan) {

    }
}
