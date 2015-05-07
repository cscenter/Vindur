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

    private static final int DOC_COUNT = 300_000;
    private static final int QUERY_COUNT = 5_000;
    private static final int UNIQUE_VALS_COUNT = 100;

    private static List<String> stringValues = new ArrayList<>();
    private static List<Integer> intValues = new ArrayList<>();
    private static List<String> luceneValues = new ArrayList<>();

    private static List<Query> queries = new ArrayList<>();

    private static void generateRandomValues(int count, StorageType type)
    {
        switch (type) {
            case RANGE_INTEGER: {
                for (int i = 0; i < 100; i++) {
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

    public static void main(String[] args) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
        System.setProperty("org.slf4j.simpleLogger.log.ru.csc", "info");
        Stopwatch timer = Stopwatch.createUnstarted();
        StorageExact stringStorage = new StorageExact<>(String.class);
        StorageRange<Integer> intStorage = new StorageRange(Integer.class);
        StorageLucene storageLucene = new StorageLucene();

        Engine engine = Engine.build()
                .executor(new SmartExecutor(3000))
                .storage("Name", stringStorage)
                .storage("Count", intStorage)
                .storage("Bio", storageLucene)
                .init();


        generateRandomValues(UNIQUE_VALS_COUNT, StorageType.RANGE_INTEGER);
        generateRandomValues(UNIQUE_VALS_COUNT, StorageType.STRING);
        generateRandomValues(UNIQUE_VALS_COUNT, StorageType.LUCENE_STRING);
        LOG.info("Documents loading...");
        timer.start();
        for (int i = 0; i < DOC_COUNT; i++)
        {
            int docId = engine.createDocument();

            int randIndex = RandomUtils.getNumber(0, 100);

            engine.setValue(docId, "Name", stringValues.get(randIndex));
            engine.setValue(docId, "Count", intValues.get(randIndex));
            engine.setValue(docId, "Bio", luceneValues.get(randIndex));
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
            int randIndex = RandomUtils.getNumber(0, 100);
            String randName = stringValues.get(randIndex);
            int randLeftBorder = RandomUtils.getNumber(0, 100);
            int randRightBorder = RandomUtils.getNumber(0, 100);
            Object randRange = StorageRange.range(Math.min(randLeftBorder, randRightBorder), Math.max(randLeftBorder, randRightBorder));
            String randText = luceneValues.get(randIndex);


            Query query = Query.build()
                    .query("Name", randName)
                    .query("Count", randRange)
                    .query("Bio", randText);
            queries.add(query);
        }
        timer.stop();
        LOG.info("{} queries generated for {} ms", QUERY_COUNT, timer.elapsed(TimeUnit.MILLISECONDS));

        LOG.info("Smart executor test");
        long totalTime = 0;

        for (Query query : queries)
        {
            timer.reset();
            timer.start();
            engine.executeQuery(query);
            timer.stop();
            totalTime += timer.elapsed(TimeUnit.MILLISECONDS);
        }

        LOG.info("{} queries executed for {} ms", QUERY_COUNT, totalTime);
        LOG.info("Average execution time is {} ms", (totalTime * 1.0f) / QUERY_COUNT);

        LOG.info("Tunable executor test");
        TunableExecutor tunableExecutor = new TunableExecutor();
        engine.setExecutor(tunableExecutor);

        totalTime = 0;

        for (Query query : queries)
        {
            timer.reset();
            timer.start();
            engine.executeQuery(query);
            timer.stop();
            totalTime += timer.elapsed(TimeUnit.MILLISECONDS);
        }

        LOG.info("{} queries executed for {} ms", QUERY_COUNT, totalTime);
        LOG.info("Average execution time is {} ms", (totalTime * 1.0f) / QUERY_COUNT);

        Tuner tuner = engine.getTuner();
        Map<String, Tuner.AttributeStat> statMap = tuner.getExecutionTimesMap();

        for (Map.Entry<String, Tuner.AttributeStat> entry : statMap.entrySet())
        {
            LOG.info("{} attribute, average exec. time is {} ns, average check time is {} ns",
                    entry.getKey(),
                    entry.getValue().executionTime,
                    entry.getValue().checkTime);
        }
    }
}
