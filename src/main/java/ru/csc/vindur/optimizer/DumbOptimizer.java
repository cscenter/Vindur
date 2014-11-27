package ru.csc.vindur.optimizer;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

import ru.csc.vindur.Request;
import ru.csc.vindur.storage.Storage;


/**
 * Created by Edgar_Work on 11.11.2014.
 */
public class DumbOptimizer implements Optimizer
{
    @SuppressWarnings("unchecked")
	@Override
    public Plan generatePlan(Request request, @SuppressWarnings("rawtypes") ConcurrentMap<String, Storage> storages)
    {
        Plan plan = new Plan();
        for (Entry<String, Object> requestPart : request.getRequestParts().entrySet())
        {
        	plan.addStep(()-> storages.get(requestPart.getKey()).findSet(requestPart.getValue()));
        }
        return plan;
    }

    @Override
    public void updatePlan(Plan plan, int currentResultSize)
    {

    }
}
