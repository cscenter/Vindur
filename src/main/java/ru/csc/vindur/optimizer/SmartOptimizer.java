package ru.csc.vindur.optimizer;

import ru.csc.vindur.Request;
import ru.csc.vindur.storage.StorageBase;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Edgar on 27.11.2014.
 */
public class SmartOptimizer implements Optimizer
{
    private int threshold = 5000;

    public SmartOptimizer(int threshold)
    {
        this.threshold = threshold;
    }

    @SuppressWarnings("unchecked")
	@Override
    public Plan generatePlan(Request request, @SuppressWarnings("rawtypes") ConcurrentMap<String, StorageBase> storages)
    {
        Map<String, Object> requestParts = new TreeMap<>((a, b) ->
                Integer.compare(storages.get(a).getComplexity() * storages.get(a).documentsCount(), storages.get(b).getComplexity() *storages.get(b).documentsCount()));

        requestParts.putAll(request.getRequestParts());

        Plan plan = new Plan();

        for (Map.Entry<String, Object> requestPart: requestParts.entrySet())
        {
            plan.addStep(() -> storages.get(requestPart.getKey()).findSet(requestPart.getValue()));
        }

        return plan;
    }

    /*не могу пока придумать, как сделать. По идее надо бы отрезать хвост и вместо поиска битсета из хранилища
    брать документ у движка и смотреть значение по соотв. атрибуту и такую лямбду добавлять в список
    */
    @Override
    public void updatePlan(Plan plan, int currentResultSize)
    {
        if (currentResultSize < this.threshold)
        {
            List<Step> tail = plan.cutTail();
            for (Step step : tail)
            {
                plan.addStep(() -> null);
            }
        }
    }
}
