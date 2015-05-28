package ru.csc.vindur.test;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.csc.vindur.Engine;
import ru.csc.vindur.Query;
import ru.csc.vindur.executor.*;
import ru.csc.vindur.executor.tuner.Tuner;
import ru.csc.vindur.storage.StorageExact;
import ru.csc.vindur.storage.StorageLucene;
import ru.csc.vindur.storage.StorageRange;
import ru.csc.vindur.storage.StorageType;
import ru.csc.vindur.test.utils.RandomUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by edgar on 26.03.15.
 */
public class TuningTest {

    private static final Logger LOG = LoggerFactory.getLogger(TuningTest.class);

    private static final int DOC_COUNT = 1_000_000;
    private static final int QUERY_COUNT = 5_000;

    private static final int STRING_VALS_COUNT = 500;
    private static final int INT_VALS_COUNT = 150;
    private static final int LUCENE_VALS_COUNT = 30;


    private static List<String> stringValues = new ArrayList<>();
    private static List<Integer> intValues = new ArrayList<>();
    private static List<String> luceneValues = new ArrayList<>();

    private static List<Query> queries = new ArrayList<>();

    private static Engine engine;

    private static void generateRandomValues(int count, StorageType type)
    {
        switch (type) {
            case RANGE_INTEGER: {
                for (int i = 0; i < count; i++) {
                    intValues.add(i);
                }
                break;
            }
            case STRING: {
                for (int i = 0; i < count; i++) {
                    stringValues.add(RandomUtils.getString(10, 50));
                }
                break;
            }
            case LUCENE_STRING: {
                for (int i = 0; i < count; i++) {
                    luceneValues.add(RandomUtils.getString(50, 200));
                }
                break;
            }
        }
    }

    private static List<List<Integer>> resultSets(Executor executor, int queryCount)
    {
        engine.setExecutor(executor);
        LOG.info("{} test", executor.getClass().getName());
        long totalTime = 0;

        Stopwatch timer = Stopwatch.createUnstarted();

        List<List<Integer>> resultSets = new ArrayList<>();
        List<Long> execTimes = new ArrayList<>();

        for (int i = 0; i < queryCount; i++) {
            Query query = queries.get(i);
            timer.reset();
            timer.start();
            List<Integer> resultSet = engine.executeQuery(query);
            timer.stop();
            resultSets.add(resultSet);
            execTimes.add(timer.elapsed(TimeUnit.MILLISECONDS));
            totalTime += timer.elapsed(TimeUnit.MILLISECONDS);
        }

        double mean = (totalTime * 1.0f) / queryCount;
        double var = 0;
        for (long t : execTimes)
        {
            var += (t - mean) * (t - mean);
        }
        var /= queryCount;

        LOG.info("{} queries executed for {} ms", queryCount, totalTime);
        LOG.info("Mean execution time is {} ms, variance is {} ms^2, standard deviation is {} ms",
                mean,
                var,
                Math.sqrt(var));

        for (int i = 0; i < 100; i++) {
            //LOG.info("Query {} returned {} records for {} ms", i + 1, resultSets.get(i).size(), execTimes.get(i));
        }

        return resultSets;
    }

    public static void main(String[] args) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
        System.setProperty("org.slf4j.simpleLogger.log.ru.csc", "info");
        Stopwatch timer = Stopwatch.createUnstarted();
        StorageExact stringStorage = new StorageExact<>(String.class);
        StorageRange<Integer> intStorage = new StorageRange(Integer.class);
        StorageLucene storageLucene = new StorageLucene();

        engine = Engine.build()
                .storage("Name", stringStorage)
                .storage("Count", intStorage)
                .storage("Bio", storageLucene)
                .init();


        generateRandomValues(INT_VALS_COUNT, StorageType.RANGE_INTEGER);
        generateRandomValues(STRING_VALS_COUNT, StorageType.STRING);
        generateRandomValues(LUCENE_VALS_COUNT, StorageType.LUCENE_STRING);
        LOG.info("Documents loading...");
        timer.start();
        for (int i = 0; i < DOC_COUNT; i++)
        {
            int docId = engine.createDocument();

            int randIntIndex = RandomUtils.getNumber(0, INT_VALS_COUNT - 1);
            int randStringIndex = RandomUtils.getNumber(0, STRING_VALS_COUNT - 1);
            int randLuceneIndex = RandomUtils.getNumber(0, LUCENE_VALS_COUNT - 1);

            engine.setValue(docId, "Name", stringValues.get(randStringIndex));
            engine.setValue(docId, "Count", intValues.get(randIntIndex));
            engine.setValue(docId, "Bio", luceneValues.get(randLuceneIndex));
        }
        timer.stop();
        long totalLoadTime = timer.elapsed(TimeUnit.MILLISECONDS);
        LOG.info("{} documents loaded for {} ms", DOC_COUNT, totalLoadTime);
        LOG.info("Average loading time is {} ms", (totalLoadTime * 1.0f) / DOC_COUNT);

        LOG.info("Queries generating...");
        timer.reset();
        timer.start();
        for (int i = 0; i < QUERY_COUNT; i++)
        {
            String randName = stringValues.get(RandomUtils.getNumber(0, STRING_VALS_COUNT - 1));
            int randLeftBorder = RandomUtils.getNumber(0, INT_VALS_COUNT);
            int randRightBorder = RandomUtils.getNumber(0, INT_VALS_COUNT);
            Object randRange = StorageRange.range(
                    Math.min(randLeftBorder, randRightBorder),
                    Math.max(randLeftBorder, randRightBorder)
            );
            String randText = luceneValues.get(RandomUtils.getNumber(0, LUCENE_VALS_COUNT - 1));


            Query query = Query.build()
                    .query("Name", randName)
                    .query("Count", randRange)
                    .query("Bio", randText);
            queries.add(query);
        }
        timer.stop();
        LOG.info("{} queries generated for {} ms", QUERY_COUNT, timer.elapsed(TimeUnit.MILLISECONDS));

        LOG.info("Warm up Vindur!");
        resultSets(new DumbExecutor(), 1500);
        resultSets(new SmartExecutor(5000), 1500);
        resultSets(new TunableExecutor(), 1500);
        LOG.info("Vindur warmed up!");

        List<List<Integer>> dumbResultSets = resultSets(new DumbExecutor(), QUERY_COUNT);
        List<List<Integer>> smartResultSets = resultSets(new SmartExecutor(6000), QUERY_COUNT);
        List<List<Integer>> tunableResultSets = resultSets(new TunableExecutor(), QUERY_COUNT);

        LOG.info("Equality test");
        LOG.info("Dumb executor's result sets equals to smart executor's {}", dumbResultSets.equals(smartResultSets));
        LOG.info("Smart executor's result sets equals to tunable executor's {}", smartResultSets.equals(tunableResultSets));
        LOG.info("Tunable executor's result sets equals to dumb executor's {}", dumbResultSets.equals(tunableResultSets));
    }
}
