package ru.csc.vindur.optimizer;

import ru.csc.vindur.Request;
import ru.csc.vindur.bitset.BitSet;
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

	@Override
    public Plan generatePlan(Request request, @SuppressWarnings("rawtypes") ConcurrentMap<String, StorageBase> storages)
    {
        Map<String, Object> requestParts = new TreeMap<>((a, b) ->
                Integer.compare(storages.get(a).getComplexity() * storages.get(a).documentsCount(), storages.get(b).getComplexity() *storages.get(b).documentsCount()));

        requestParts.putAll(request.getRequestParts());

        List<Step> steps = Optimizer.requestPartsToSteps(requestParts, storages);

        return new SimplePlan(steps);
    }

    @Override
    public void updatePlan(Plan plan, BitSet currentResult)
    {
        if (currentResult.cardinality() < this.threshold)
        {
        	// TODO
        }
    }
}
