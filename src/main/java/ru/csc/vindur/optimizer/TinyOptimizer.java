package ru.csc.vindur.optimizer;

import ru.csc.vindur.EngineConfig;
import ru.csc.vindur.Request;
import ru.csc.vindur.storage.Storage;

import java.util.*;

/**
 * Created by Edgar on 26.10.14.
 */

public class TinyOptimizer implements Optimizer {
    private Map<String, Storage> indexes;
    private SortedMap<Request.RequestPart, Long> complexityList;
    private final EngineConfig engineConfig;

    public TinyOptimizer(Map<String, Storage> indexes, EngineConfig engineConfig) {
        this.indexes = indexes;
        this.engineConfig = engineConfig;
    }

    @Override
    public Plan generatePlan(Request request) {
        /*for each request part get index, if index is not null, get expectedAmount() else do full scan
         *(or set expectedAmount to very big constant), then sort by expectedAmount() and return such plan
         */
        Map<Request.RequestPart, Long> unsortedComplexityList = new HashMap<>();
        for (Request.RequestPart requestPart : request.getRequestParts()) {
            Storage index = indexes.get(requestPart.getTag());
            if (index != null) {
                unsortedComplexityList.put(requestPart, index.size());
            } else { //todo если индекса нет, как по нему можно искать. Нужно выдавать ошибку
                unsortedComplexityList.put(requestPart, Long.MAX_VALUE);
            }
        }

        ValueComparator valueComparator = new ValueComparator(unsortedComplexityList);
        this.complexityList = new TreeMap<>(valueComparator);
        this.complexityList.putAll(unsortedComplexityList);

        Plan plan = new Plan();

        for (Map.Entry<Request.RequestPart, Long> entry : complexityList.entrySet()) {
            Step step = entry.getValue() < 5000 ? new FilterStep(entry.getKey(), plan, engineConfig) : new PatternStep(entry.getKey(), plan, engineConfig);
            plan.addStep(step);
        }

        return plan;
    }

    @Override
    public void updatePlan(Plan plan) {

    }


    //todo лучше бы заменить на lamda
    private class ValueComparator implements Comparator<Request.RequestPart> {
        private Map<Request.RequestPart, Long> map;

        public ValueComparator(Map<Request.RequestPart, Long> map) {
            this.map = map;
        }

        @Override
        public int compare(Request.RequestPart o1, Request.RequestPart o2) {
            if (map.get(o1) >= map.get(o2)) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
