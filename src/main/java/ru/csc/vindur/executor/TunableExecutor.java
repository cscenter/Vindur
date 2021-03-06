package ru.csc.vindur.executor;

import com.google.common.collect.Lists;
import ru.csc.vindur.Engine;
import ru.csc.vindur.Query;
import ru.csc.vindur.bitset.BitArray;
import ru.csc.vindur.bitset.ROBitArray;
import ru.csc.vindur.executor.tuner.Tuner;
import ru.csc.vindur.storage.Storage;

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
    private volatile Map<String, Tuner.AttributeStat> complexitiesMap;

    @Override
    @SuppressWarnings({"unchecked"})
    public BitArray execute(Query query, Engine engine)
    {
        Tuner tuner = engine.getTuner();
        this.complexitiesMap = tuner.getExecutionTimesMap();

        //todo: think about it
        Comparator<String> compare =
                (a, b) -> {
                    if (this.complexitiesMap.get(a) == null || this.complexitiesMap.get(b) == null) {
                        return Long.compare(
                                engine.getStorages().get(a).getComplexity(),
                                engine.getStorages().get(b).getComplexity()
                        );
                    } else {
                        return Long.compare(
                                this.complexitiesMap.get(a).executionTime,
                                this.complexitiesMap.get(b).executionTime
                        );
                    }
                };


        List<String> attributes = Lists.newArrayList(query.getQueryParts().keySet());
        Collections.sort(attributes, compare);

        BitArray resultSet = null;

        long estimatedExecutionTime = 0;
        long estimatedCheckTime = 0;

        for (String attribute : attributes)
        {
            if (complexitiesMap.get(attribute) == null)
            {
                estimatedExecutionTime += engine.getStorages().get(attribute).getComplexity();
                estimatedCheckTime += engine.getStorages().get(attribute).getComplexity();
            }
            else
            {
                estimatedExecutionTime += complexitiesMap.get(attribute).executionTime;
                estimatedCheckTime += complexitiesMap.get(attribute).checkTime;
            }
        }

        for (int i = 0; i < attributes.size(); ++i)
        {
            String key = attributes.get(i);
            Tuner.AttributeStat stat = complexitiesMap.get(key);
            Storage storage = engine.getStorages().get(key);


            ROBitArray stepResult = storage.findSet(query.getQueryParts().get(key));
            if (resultSet == null)
            {
                resultSet = stepResult.copy();
                //continue;
            }

            resultSet = resultSet.and(stepResult);

            if (resultSet.cardinality() == 0)
                return BitArray.create();

            if (stat == null)
            {
                estimatedCheckTime -= storage.getComplexity();
                estimatedExecutionTime -= storage.getComplexity();
            }
            else
            {
                estimatedCheckTime -= stat.checkTime;
                estimatedExecutionTime -= stat.executionTime;
            }


            if (resultSet.cardinality() * estimatedCheckTime < estimatedExecutionTime)
            {
                List<String> tail = cutTail(attributes, i + 1);
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
        BitArray resultSet = null;
        for (String key : tail)
        {
            BitArray stepResult = BitArray.create();
            for (int docId : currentResult.toIntList())
            {
                List<Object> values = engine.getDocument(docId).getValues(key);
                Object request = query.getQueryParts().get(key);
                if (values != null)
                {
                    for (Object value : values)
                    {
                        if (engine.getStorages().get(key).checkValue(docId, value, request))
                        {
                            stepResult.set(docId);
                        }
                    }
                }
            }
            if (resultSet == null) resultSet = stepResult.copy();

            resultSet = resultSet.and(stepResult);
        }
        return resultSet.and(currentResult);
    }
}
