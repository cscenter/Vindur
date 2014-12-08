package ru.csc.vindur.executor;

import java.util.Map;
import java.util.TreeMap;

import ru.csc.vindur.Engine;
import ru.csc.vindur.Query;
import ru.csc.vindur.bitset.BitArray;
import ru.csc.vindur.bitset.ROBitArray;

/**
 * Created by Edgar on 27.11.2014.
 */
public class TinyExecutor implements Executor {
    @Override
    @SuppressWarnings({ "unchecked" })
    public BitArray execute(Query query, Engine engine) {
        Map<String, Object> queryParts = new TreeMap<>(
                (a, b) -> Integer.compare(engine.getStorages().get(a)
                        .documentsCount(), engine.getStorages().get(b)
                        .documentsCount()));

        queryParts.putAll(query.getQueryParts());

        BitArray resultSet = null;
        for (Map.Entry<String, Object> entry : queryParts.entrySet()) {
            ROBitArray stepResult = engine.getStorages().get(entry.getKey())
                    .findSet(entry.getValue());
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
