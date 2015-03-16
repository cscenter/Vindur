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
public class Tuner
{
    //todo: все еще не понимаю, откуда их брать =(((
    private volatile ConcurrentLinkedQueue<Pair<Query, Engine>> queries;
    private volatile ConcurrentHashMap<String, Long> complexitiesMap;


    /* attribute -> findSet() execution times by this attribute
         * not volatile because available only in this thread
         */
    private Map<String, AttributeStat> executionTimesMap = new HashMap<>();
    /* attribute -> checkValue() execution times by this attribute
     * also not volatile because available only here
     */
    private Engine engine;

    public Tuner(Engine engine)
    {
        this.engine = engine;
    }

    public Map<String, AttributeStat> getExecutionTimesMap() {
        return executionTimesMap;
    }

    public void call() throws Exception //attribute->ms exec time
    {
        if (queries.isEmpty()) return;
        Query query = queries.peek().getKey();
        Stopwatch timer = Stopwatch.createUnstarted();

        for (String attribute : query.getQueryParts().keySet() )
        {
            Storage storage = this.engine.getStorages().get(attribute);
            timer.start();
            ROBitArray resultSet = storage.findSet(query.getQueryParts().get(attribute));
            timer.stop();

            Long res = timer.elapsed(TimeUnit.NANOSECONDS);
            Long res2=null;

            if (resultSet.cardinality() != 0)
            {
                Random random = new Random();
                //random document
                int docID = resultSet.toIntList().get(random.nextInt(resultSet.cardinality()));
                Object value = this.engine.getDocument(docID).getValues(attribute).get(0);
                timer.start();
                storage.checkValue(docID, value, query.getQueryParts().get(attribute));
                timer.stop();
                res2 = timer.elapsed(TimeUnit.NANOSECONDS);
            }
            AttributeStat.updateMap(executionTimesMap,attribute,res,res2);
        }
    }

    public void addQuery(Query query)
    {
        if (queries.isEmpty())
        queries.add(new Pair<>(query, this.engine));
    }

    public static class AttributeStat
    {
        public long executionTime=0;
        public long executionAttempts=0;
        public long checkTime=0;
        public long checkAttempts=0;

        public void update(Long execTime, Long chckTime)
        {
            if (execTime != null)
            {
                executionTime = (executionAttempts * executionTime + execTime) / (executionAttempts + 1);
                executionAttempts++;
            }
            if (chckTime != null)
            {
                checkTime = (checkAttempts * checkTime + chckTime) / (checkAttempts + 1);
                checkAttempts++;
            }
        }

       public static void updateMap(Map<String,AttributeStat> map, String key, Long execTime, Long chkTime)
       {
           AttributeStat as = map.get(key);
           if (as==null)
           {
               as = new AttributeStat();
               map.put(key,as);
           }
          as.update(execTime, chkTime);
       }

    }

}
