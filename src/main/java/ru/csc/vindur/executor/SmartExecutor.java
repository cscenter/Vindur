package ru.csc.vindur.executor;

import java.util.*;

import com.google.common.collect.Lists;
import ru.csc.vindur.Engine;
import ru.csc.vindur.Query;
import ru.csc.vindur.bitset.BitArray;
import ru.csc.vindur.bitset.ROBitArray;

/**
 * Created by Edgar on 27.11.2014.
 */
public class SmartExecutor implements Executor {
    private int threshold;

    public SmartExecutor(int threshold) {
        this.threshold = threshold;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public BitArray execute(Query query, Engine engine) {
        Comparator<String> compare =
                (a, b) -> Long.compare(
                        engine.getStorages().get(a).getComplexity(),
                        engine.getStorages().get(b).getComplexity()
                );

        List<String> reqs = Lists.newArrayList(query.getQueryParts().keySet());
        Collections.sort(reqs, compare);

        BitArray resultSet = null;
        for (int i = 0; i < reqs.size(); ++i)
        {
            String key = reqs.get(i);
            ROBitArray stepResult = engine.getStorages().get(key)
                    .findSet(query.getQueryParts().get(key));
            if (resultSet == null) {
                resultSet = stepResult.copy();
                //continue;
            }
            if (resultSet.cardinality() == 0)
                return null;

            resultSet = resultSet.and(stepResult);

            if (resultSet.cardinality() < this.threshold)
            {
                List<String> tail = cutTail(reqs, i + 1);
                if (tail.size() != 0)
                    return checkManually(tail, query, engine, resultSet);
            }
        }

        return resultSet;

    }

    private List<String> cutTail(List<String> list, int fromIndex)
    {
        List<String> tail = Lists.newArrayList();
        for (int i = fromIndex; i < list.size(); i++)
        {
            tail.add(list.get(i));
        }
        return tail;
    }

    @SuppressWarnings({"unchecked"})
    private BitArray checkManually(List<String> tail, Query query,Engine engine, ROBitArray currentResult)
    {
        BitArray resultSet = BitArray.create();
        for (String key : tail)
        {
            for (int docId : currentResult.toIntList())
            {
                //todo: разобраться с запросами по диапазону =(
                //todo: вроде разобрался =)
                List<Object> values = engine.getDocument(docId).getValues(key);
                Object request = query.getQueryParts().get(key);
                if (values != null)
                {
                    for (Object value : values)
                    {
                        if (engine.getStorages().get(key).checkValue(docId, value, request))
                        {
                            resultSet.set(docId);
                        }
                    }
                }
            }
        }

        return resultSet.and(currentResult);
    }
}
