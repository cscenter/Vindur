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
    @SuppressWarnings({"unchecked"})
    public BitSet execute(Query query, Engine engine) {
        Map<String, Object> queryParts = new TreeMap<>(
                (a, b) -> Integer.compare(engine.getStorages().get(a)
                        .getComplexity()
                        * engine.getStorages().get(a).documentsCount(), engine
                        .getStorages().get(b).getComplexity()
                        * engine.getStorages().get(b).documentsCount()));

        queryParts.putAll(query.getQueryParts());

        BitSet resultSet = null;
        for (Map.Entry<String, Object> entry : queryParts.entrySet())
        {
            ROBitSet stepResult = engine.getStorages().get(entry.getKey())
                    .findSet(entry.getValue());
            if (resultSet == null) {
                resultSet = stepResult.copy();
                continue;
            }
            if (resultSet.cardinality() == 0)
                return null;

            resultSet = resultSet.and(stepResult);

            if (resultSet.cardinality() >= this.threshold)
               continue;

            //если выборка маленькая - проверяем ручками
            //todo нужно сделать отедльный метод:
            // проверять значения в storage, если подходит - внести в новый битсет
            // и сделать unitTest
            // и проверить, что это точно последний шаг!

            for (int docId : resultSet.toIntList())
            {
                    if (engine.getDocument(docId)
                            .getValuesByAttribute(entry.getKey())
                            .contains(entry.getValue()))
                        resultSet.set(docId);

                }

        }

        return resultSet;

    }
}
