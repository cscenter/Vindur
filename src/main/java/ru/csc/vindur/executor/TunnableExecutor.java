package ru.csc.vindur.executor;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import ru.csc.vindur.Engine;
import ru.csc.vindur.Query;
import ru.csc.vindur.bitset.BitArray;
import ru.csc.vindur.bitset.ROBitArray;
import ru.csc.vindur.storage.Storage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Edgar on 11.02.2015.
 */
public class TunnableExecutor implements Executor
{
    private int threshold;
    private volatile boolean isFree;
    private Thread worker;
    //todo: where i should init it?
    private ConcurrentLinkedQueue<Query> queries;
    private volatile ConcurrentHashMap<Storage, Long> complexitiesMap;

    public TunnableExecutor(int threshold)
    {
        this.threshold = threshold;
        this.isFree = true;

        this.worker = new Thread(() ->
        {

            //todo: possible exception can be thrown somewhere here
            while (true)
            {
                isFree = false;
                Query query = queries.peek();
                Stopwatch timer = Stopwatch.createUnstarted();
                timer.start();
                Engine engine = Engine.build().init();
                this.execute(query, engine);
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                timer.stop();
                //todo: not sure about this =(
                for (String attribute: query.getQueryParts().keySet())
                {
                    Storage key = engine.getStorages().get(attribute);
                    this.complexitiesMap.put(key, timer.elapsed(TimeUnit.MILLISECONDS));
                }
                isFree = true;
            }
        });
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public BitArray execute(Query query, Engine engine)
    {
        this.isFree = true;
        //todo: think about it
        Comparator<String> compare =
                (a, b) ->
                {
                    Storage first = engine.getStorages().get(a);
                    Storage second = engine.getStorages().get(b);

                    return Long.compare(
                            this.complexitiesMap.get(first),
                            this.complexitiesMap.get(second)
                    );
                };

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
