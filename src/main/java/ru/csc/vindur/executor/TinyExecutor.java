package ru.csc.vindur.executor;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;

import ru.csc.vindur.Engine;
import ru.csc.vindur.Query;
import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.ROBitSet;
import ru.csc.vindur.storage.StorageBase;

/**
 * Created by Edgar on 27.11.2014.
 */
public class TinyExecutor implements Executor
{
    @Override
    public BitSet execute(Query query, Engine engine)
    {
        Map<String, Object> queryParts = new TreeMap<>(
                (a, b) -> Integer.compare(engine.getColumns().get(a).documentsCount(),
                        engine.getColumns().get(b).documentsCount()));

        queryParts.putAll(query.getQueryParts());

        List<Step> steps = Executor.requestPartsToSteps(queryParts, engine.getColumns());

        Plan plan = new SimplePlan(steps);

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
            step = plan.next();
        }

        return resultSet;
    }

}
