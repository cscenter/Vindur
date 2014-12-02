package ru.csc.vindur.executor;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import ru.csc.vindur.Engine;
import ru.csc.vindur.Query;
import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.EWAHBitSet;
import ru.csc.vindur.bitset.ROBitSet;
import ru.csc.vindur.storage.StorageBase;

/**
 * Created by Edgar_Work on 11.11.2014.
 */
public class DumbExecutor implements Executor
{
    @Override
    public BitSet execute(Query query, Engine engine)
    {
        Plan plan = generatePlan(query, engine.getColumns());

        Step step = plan.next();
        BitSet resultSet = null;
        while (step != null) {
            ROBitSet stepResult = step.execute();
            if (resultSet == null) {
                resultSet = stepResult.copy();
            } else {
                resultSet = resultSet.and(stepResult);
            }
            if (resultSet.cardinality() == 0) {
                return null;
            }
            updatePlan(plan, resultSet);
            step = plan.next();
        }

        if (resultSet == null) {
            return null;
        }

        return resultSet;
    }

    @Override
    public Plan generatePlan(
            Query query,
            @SuppressWarnings("rawtypes") ConcurrentMap<String, StorageBase> storages) {
        List<Step> steps = Executor.requestPartsToSteps(
                query.getQueryParts(), storages);
        return new SimplePlan(steps);
    }

    @Override
    public void updatePlan(Plan plan, BitSet currentResult) {

    }
}
