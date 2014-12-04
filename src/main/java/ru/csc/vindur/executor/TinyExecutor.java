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
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public BitSet execute(Query query, Engine engine)
    {
        Map<String, Object> queryParts = new TreeMap<>(
                (a, b) -> Integer.compare(engine.getColumns().get(a).documentsCount(),
                        engine.getColumns().get(b).documentsCount()));

        queryParts.putAll(query.getQueryParts());

        BitSet resultSet = null;
        for (Map.Entry<String, Object> entry : queryParts.entrySet()) {
            ROBitSet stepResult = engine.getColumns().get(entry.getKey()).findSet(entry.getValue());
            if (resultSet == null) {
                resultSet = stepResult.copy();
            } else {
                resultSet = resultSet.and(stepResult);
                if (resultSet.cardinality() == 0) {
                    return null;
                }
            }
        }

        return resultSet;
    }

}
