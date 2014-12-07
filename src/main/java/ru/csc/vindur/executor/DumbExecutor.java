package ru.csc.vindur.executor;

import java.util.Map;

import ru.csc.vindur.Engine;
import ru.csc.vindur.Query;
import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.ROBitSet;

/**
 * Created by Edgar_Work on 11.11.2014.
 */
public class DumbExecutor implements Executor {
    @SuppressWarnings("unchecked")
    @Override
    public BitSet execute(Query query, Engine engine) {
        BitSet resultSet = null;
        for (Map.Entry<String, Object> entry : query.getQueryParts().entrySet()) {
            ROBitSet stepResult = engine.getColumns().get(entry.getKey())
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
