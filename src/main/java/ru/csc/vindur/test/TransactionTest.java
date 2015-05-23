package ru.csc.vindur.test;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.csc.vindur.Engine;
import ru.csc.vindur.Query;
import ru.csc.vindur.executor.DumbExecutor;
import ru.csc.vindur.executor.SmartExecutor;
import ru.csc.vindur.storage.StorageExact;
import ru.csc.vindur.storage.StorageLucene;
import ru.csc.vindur.storage.StorageRange;
import ru.csc.vindur.storage.StorageType;
import ru.csc.vindur.test.utils.RandomUtils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by edgar on 15.05.15.
 */
public class TransactionTest {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionTest.class);

    private static final int DOC_COUNT = 300_000;
    private static final int QUERY_COUNT = 5_000;
    private static final int UNIQUE_VALS_COUNT = 100;

    private static List<String> stringValues = new ArrayList<>();
    private static List<Integer> intValues = new ArrayList<>();
    private static List<String> luceneValues = new ArrayList<>();

    private static List<Query> queries = new ArrayList<>();

    private static List<Long> deltas = new ArrayList<>();

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

    private static String randomAttribute()
    {
        int i = RandomUtils.getNumber(0, 2);
        switch (i)
        {
            case 0 :
            {
                return "Name";
            }
            case 1 :
            {
                return "Count";
            }
            case 2 :
            {
                return "Bio";
            }
            default:
            {
                return "Name";
            }
        }
    }

    private static Object randomValue(String attribute)
    {
        switch (attribute)
        {
            case "Name" :
            {
                return RandomUtils.getString(10, 50);
            }
            case "Count" :
            {
                return RandomUtils.getNumber(0, 100);
            }
            case "Bio" :
            {
                return RandomUtils.getString(50, 200);
            }
            default :
            {
                return null;
            }
        }
    }


    public static void main(String[] args)
    {
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
            Object randRange = StorageRange.range(
                    Math.min(randLeftBorder, randRightBorder),
                    Math.max(randLeftBorder, randRightBorder)
            );
            String randText = luceneValues.get(randIndex);


            Query query = Query.build()
                    .query("Name", randName)
                    .query("Count", randRange)
                    .query("Bio", randText);
            queries.add(query);
        }
        timer.stop();
        LOG.info("{} queries generated for {} ms", QUERY_COUNT, timer.elapsed(TimeUnit.MILLISECONDS));

        LOG.info("Warm up Vidur!");
        for (int i = 0; i < 20; i++)
        {
            long tID = engine.startTransaction();
            for (int j = 0; j < 100; j++)
            {
                int randDocID = RandomUtils.getNumber(1, DOC_COUNT);
                String randAttribute = randomAttribute();
                Object randValue = randomValue(randAttribute);
                engine.setValue(tID, randDocID, randAttribute, randValue);
            }
            engine.commitTransaction(tID);
        }
        queries.forEach(engine::executeQuery);
        LOG.info("Vindur warmed up");

        LOG.info("Executing first 500 queries");
        for (int i = 0; i < 500; i++)
        {
            Query q = queries.get(i);
            timer.reset();
            timer.start();
            engine.executeQuery(q);
            timer.stop();
            deltas.add(timer.elapsed(TimeUnit.NANOSECONDS));
        }
        LOG.info("First 500 queries executed");


        long trID = engine.startTransaction();
        LOG.info("Transaction number {} started", trID);

        LOG.info("Applying changes");
        for (int i = 0; i < 5e4; i++) {
            //apply changes
            int randDocID = RandomUtils.getNumber(1, DOC_COUNT);
            String randAttribute = randomAttribute();
            Object randValue = randomValue(randAttribute);
            engine.setValue(trID, randDocID, randAttribute, randValue);
        }
        LOG.info("Changes applied");

        LOG.info("Commiting transaction");
        timer.reset();
        timer.start();
        engine.commitTransaction(trID);
        timer.stop();
        LOG.info("Transaction commited");
        deltas.add(timer.elapsed(TimeUnit.NANOSECONDS));

        LOG.info("Executing rest of queries");
        for (int i = 500; i < QUERY_COUNT; i++)
        {
            Query query = queries.get(i);
            timer.reset();
            timer.start();
            engine.executeQuery(query);
            timer.stop();
            deltas.add(timer.elapsed(TimeUnit.NANOSECONDS));
        }
        LOG.info("Rest of queries executed");

        LOG.info("Writing results");
        try
        {
            PrintWriter writer = new PrintWriter("NewTransactions.csv");
            for (long delta : deltas)
            {
                writer.format("%d\n", delta);
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        LOG.info("Results are written");
    }
}
