package ru.csc.vindur.optimizer;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import ru.csc.vindur.Request;
import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.storage.StorageBase;

/**
 * Created by Edgar_Work on 11.11.2014.
 */
public class DumbOptimizer implements Optimizer {
    @Override
    public Plan generatePlan(
            Request request,
            @SuppressWarnings("rawtypes") ConcurrentMap<String, StorageBase> storages) {
        List<Step> steps = Optimizer.requestPartsToSteps(
                request.getRequestParts(), storages);
        return new SimplePlan(steps);
    }

    @Override
    public void updatePlan(Plan plan, BitSet currentResult) {

    }
}
