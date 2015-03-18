package ru.csc.vindur.executor;

import com.google.common.collect.Lists;
import ru.csc.vindur.Engine;
import ru.csc.vindur.Query;
import ru.csc.vindur.bitset.BitArray;
import ru.csc.vindur.bitset.ROBitArray;
import ru.csc.vindur.executor.tuner.Tuner;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Edgar on 11.02.2015.
 */
public class TunableExecutor implements Executor
{
    private Map<String, Tuner.AttributeStat> complexitiesMap;

    @Override
    @SuppressWarnings({"unchecked"})
    public BitArray execute(Query query, Engine engine)
    {
        engine.getTuner().addQuery(query);
        this.complexitiesMap = engine.getTuner().getExecutionTimesMap();

        //todo: think about it
        Comparator<String> compare =
                (a, b) ->
                        Long.compare(
                                this.complexitiesMap.get(a).executionTime,
                                this.complexitiesMap.get(b).executionTime
                        );

        List<String> reqs = Lists.newArrayList(query.getQueryParts().keySet());
        Collections.sort(reqs, compare);

        BitArray resultSet = null;
        for (int i = 0; i < reqs.size(); ++i)
        {
            String key = reqs.get(i);
            ROBitArray stepResult = engine.getStorages().get(key)
                    .findSet(query.getQueryParts().get(key));
            if (resultSet == null)
            {
                resultSet = stepResult.copy();
                //continue;
            }
            if (resultSet.cardinality() == 0)
                return null;

            resultSet = resultSet.and(stepResult);

            int partsLeft = reqs.size() - i + 1;

            long estimatedCheckTime = partsLeft * complexitiesMap.get(key).checkTime;
            long estimatedExecutionTime = 0;

            for (int j = i; j < reqs.size(); j++)
                estimatedExecutionTime += complexitiesMap.get(reqs.get(j)).executionTime;

            if (estimatedCheckTime < estimatedExecutionTime)
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
