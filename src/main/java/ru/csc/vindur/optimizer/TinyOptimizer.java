package ru.csc.vindur.optimizer;

import ru.csc.vindur.Request;
import ru.csc.vindur.storage.Storage;


import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Created by Edgar on 27.11.2014.
 */
public class TinyOptimizer implements Optimizer
{
    @Override
    public Plan generatePlan(Request request, @SuppressWarnings("rawtypes") ConcurrentMap<String, Storage> storages)
    {
        Map<String, Object> requestParts = new TreeMap<>((a, b) ->
                Integer.compare(storages.get(a).documentsCount(), storages.get(b).documentsCount()));

        requestParts.putAll(request.getRequestParts());

        Plan plan = new Plan();

        for (Map.Entry<String, Object> requestPart: requestParts.entrySet())
        {
            plan.addStep(() -> storages.get(requestPart.getKey()).findSet(requestPart.getValue()));
        }

        return plan;
    }

    @Override
    public void updatePlan(Plan plan, int currentResultSize)
    {

    }
}
