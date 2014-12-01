package ru.csc.vindur.optimizer;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;

import ru.csc.vindur.Engine;
import ru.csc.vindur.Request;
import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.storage.StorageBase;

/**
 * Created by Edgar on 27.11.2014.
 */
public class SmartOptimizer implements Optimizer {
    private int threshold = 5000;
    private Engine engine;

    public SmartOptimizer(int threshold, Engine engine) {
        this.threshold = threshold;
        this.engine = engine;
    }

    @Override
    public Plan generatePlan(
            Request request,
            @SuppressWarnings("rawtypes") ConcurrentMap<String, StorageBase> storages) {
        Map<String, Object> requestParts = new TreeMap<>(
                (a, b) -> Integer.compare(storages.get(a).getComplexity()
                        * storages.get(a).documentsCount(), storages.get(b)
                        .getComplexity() * storages.get(b).documentsCount()));

        requestParts.putAll(request.getRequestParts());

        List<Step> steps = Optimizer
                .requestPartsToSteps(requestParts, storages);

        return new SimplePlan(steps);
    }

    @Override
    public void updatePlan(Plan plan, BitSet currentResult) {
        if (currentResult.cardinality() < this.threshold) {
            List<Step> tail = plan.cutTail();
            for (int i = 0; i < tail.size(); ++i) {
                plan.addStep(() -> {
                    for (int docId : currentResult.toIntList()) {
                        // todo: а откуда брать атрибут и значение?
                        /*
                         * if
                         * (engine.getDocuments().get(docId).getValuesByAttribute
                         * (attr).contains(val)) { currentResult.set(docId); }
                         */

                    }
                    return currentResult;
                });
            }
        }
    }
}
