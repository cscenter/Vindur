package ru.csc.vindur.optimizer;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;

import ru.csc.vindur.Request;
import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.storage.StorageBase;

/**
 * Created by Edgar on 27.11.2014.
 */
public class TinyOptimizer implements Optimizer {
    @Override
    public Plan generatePlan(
            Request request,
            @SuppressWarnings("rawtypes") ConcurrentMap<String, StorageBase> storages) {
        Map<String, Object> requestParts = new TreeMap<>(
                (a, b) -> Integer.compare(storages.get(a).documentsCount(),
                        storages.get(b).documentsCount()));

        requestParts.putAll(request.getRequestParts());

        List<Step> steps = Optimizer.requestPartsToSteps(
                request.getRequestParts(), storages);

        return new SimplePlan(steps);
    }

    @Override
    public void updatePlan(Plan plan, BitSet currentResult) {

    }
}
