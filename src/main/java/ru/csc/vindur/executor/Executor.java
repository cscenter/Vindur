package ru.csc.vindur.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import ru.csc.vindur.Engine;
import ru.csc.vindur.Query;
import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.storage.StorageBase;

/**
 * Created by Edgar on 26.10.14.
 */
public interface Executor
{
    /**
     * @param query
     * @param engine
     * @return
     */

    public BitSet execute(Query query, Engine engine);

    public Plan generatePlan(
            Query query,
            @SuppressWarnings("rawtypes") ConcurrentMap<String, StorageBase> storages);

    public void updatePlan(Plan plan, BitSet currentResult);

    @SuppressWarnings({ "rawtypes", "unchecked" })
    static List<Step> requestPartsToSteps(Map<String, Object> requestParts,
            ConcurrentMap<String, StorageBase> storages) {
        List<Step> steps = new ArrayList<>(requestParts.size());
        for (Map.Entry<String, Object> requestPart : requestParts.entrySet()) {
            steps.add(() -> storages.get(requestPart.getKey()).findSet(
                    requestPart.getValue()));
        }
        return steps;
    }
}
