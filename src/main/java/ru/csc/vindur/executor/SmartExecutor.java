package ru.csc.vindur.executor;

import java.util.Map;
import java.util.TreeMap;

import ru.csc.vindur.Engine;
import ru.csc.vindur.Query;
import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.ROBitSet;

/**
 * Created by Edgar on 27.11.2014.
 */
public class SmartExecutor implements Executor {
    private int threshold = 5000;

    public SmartExecutor(int threshold) {
        this.threshold = threshold;
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public BitSet execute(Query query, Engine engine) {
        Map<String, Object> queryParts = new TreeMap<>(
                (a, b) -> Integer.compare(engine.getColumns().get(a)
                        .getComplexity()
                        * engine.getColumns().get(a).documentsCount(), engine
                        .getColumns().get(b).getComplexity()
                        * engine.getColumns().get(b).documentsCount()));

        queryParts.putAll(query.getQueryParts());

        BitSet resultSet = null;
        for (Map.Entry<String, Object> entry : queryParts.entrySet()) {
            ROBitSet stepResult = engine.getColumns().get(entry.getKey())
                    .findSet(entry.getValue());
            if (resultSet == null) {
                resultSet = stepResult.copy();
            } else {
                resultSet = resultSet.and(stepResult);
                if (resultSet.cardinality() < this.threshold) {
                    for (int docId : resultSet.toIntList()) {
                        if (engine.getDocuments().get(docId)
                                .getValuesByAttribute(entry.getKey())
                                .contains(entry.getValue())) {
                            resultSet.set(docId);
                        }
                    }
                }
                if (resultSet.cardinality() == 0) {
                    return null;
                }
            }
        }

        return resultSet;

    }
}
