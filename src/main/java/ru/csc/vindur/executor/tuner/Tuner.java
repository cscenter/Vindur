package ru.csc.vindur.executor.tuner;

import com.google.common.base.Stopwatch;
import javafx.util.Pair;
import ru.csc.vindur.Engine;
import ru.csc.vindur.Query;
import ru.csc.vindur.bitset.ROBitArray;
import ru.csc.vindur.storage.Storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Edgar on 19.02.2015.
 */
public class Tuner implements Callable<ConcurrentHashMap<String, Long>>
{
    public volatile boolean isFree;

    //todo: все еще не понимаю, откуда их брать =(((
    private volatile ConcurrentLinkedQueue<Pair<Query, Engine>> queries;
    private volatile ConcurrentHashMap<String, Long> complexitiesMap;

    /* attribute -> findSet() execution times by this attribute
     * not volatile because available only in this thread
     */
    private Map<String, ArrayList<Long>> executionTimesMap = new HashMap<>();
    /* attribute -> checkValue() execution times by this attribute
     * also not volatile because available only here
     */
    private Map<String, ArrayList<Long>> checkTimesMap = new HashMap<>();
    private Engine engine;

    public Tuner(Engine engine)
    {
        this.engine = engine;
    }

    //это и есть tune()
    @Override
    @SuppressWarnings("Unchecked")
    public ConcurrentHashMap<String, Long> call() throws Exception
    {
        isFree = false;
        Query query = queries.peek().getKey();
        if (query == null)
        {
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        Stopwatch timer = Stopwatch.createUnstarted();


        for (String attribute : query.getQueryParts().keySet() )
        {
            Storage storage = this.engine.getStorages().get(attribute);
            timer.start();
            ROBitArray resultSet = storage.findSet(query.getQueryParts().get(attribute));
            timer.stop();
            if (executionTimesMap.containsKey(attribute))
            {
                executionTimesMap.get(attribute).add(timer.elapsed(TimeUnit.NANOSECONDS));
            }
            else
            {
                executionTimesMap.put(attribute, new ArrayList<>());
                executionTimesMap.get(attribute).add(timer.elapsed(TimeUnit.NANOSECONDS));
            }
            if (resultSet.cardinality() != 0)
            {
                Random random = new Random();
                //random document
                int docID = resultSet.toIntList().get(random.nextInt(resultSet.cardinality()));
                Object value = this.engine.getDocument(docID).getValues(attribute).get(0);
                timer.start();
                storage.checkValue(docID, value, query.getQueryParts().get(attribute));
                timer.stop();
                //not sure, what to do after - add two average times?
                if (checkTimesMap.containsKey(attribute))
                {
                    checkTimesMap.get(attribute).add(timer.elapsed(TimeUnit.NANOSECONDS));
                }
                else
                {
                    checkTimesMap.put(attribute, new ArrayList<>());
                    checkTimesMap.get(attribute).add(timer.elapsed(TimeUnit.NANOSECONDS));
                }
            }
        }

        for (String attribute : executionTimesMap.keySet())
        {
            Long res = (long)executionTimesMap.get(attribute).stream().mapToLong(i -> i).average().orElse(0);
            complexitiesMap.put(attribute, res);
        }
        isFree = true;
        return complexitiesMap;
    }

    public void addQuery(Query query)
    {
        queries.add(new Pair<>(query, this.engine));
    }
}
