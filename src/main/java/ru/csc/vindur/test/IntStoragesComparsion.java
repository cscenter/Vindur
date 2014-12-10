package ru.csc.vindur.test;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.csc.vindur.Engine;
import ru.csc.vindur.Query;
import ru.csc.vindur.storage.RangeRequest;
import ru.csc.vindur.storage.StorageBucketIntegers;
import ru.csc.vindur.storage.StorageRange;
import ru.csc.vindur.test.utils.RandomUtils;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Andrey Kokorev
 *         Created on 10.12.2014.
 */
public class IntStoragesComparsion
{
    private static final Logger LOG = LoggerFactory
            .getLogger(IntStoragesComparsion.class);
    static final int DOC_NUM = 100_000;
    static final int MAX_VAL = 10_000;
    static final int VAL_NUM = 2 * MAX_VAL;
    static final int QUERY_NUM = 10_000;
    public void run()
    {
        Engine engine = Engine.build()
                            .storage("warmupRange", new StorageRange<>(Integer.class))
                            .storage("warmupBuckets", new StorageBucketIntegers())
                            .storage("rangeU", new StorageRange<>(Integer.class))
                            .storage("bucketsU", new StorageBucketIntegers())
                            .storage("rangeG", new StorageRange<>(Integer.class))
                            .storage("bucketsG", new StorageBucketIntegers())
                            .init();
        LOG.info("Generating values", DOC_NUM);
        Stopwatch stopwatch = Stopwatch.createStarted();

        Integer[] gaussian = new Integer[DOC_NUM];
        Integer[] uniform = new Integer[DOC_NUM];
        generateData(uniform, gaussian);

        stopwatch.stop();
        LOG.info("Values generated in {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));

        LOG.info("Loading {} values", DOC_NUM);
        LOG.info("Test loading");
        LOG.info("Warmup");
        loadData(engine, stopwatch, "warmupRange", gaussian);
        loadData(engine, stopwatch, "warmupBuckets", gaussian);


        LOG.info("Test 1. Uniform distribution at [{}, {}]", -MAX_VAL, MAX_VAL);
        LOG.info(" Loading to StorageRange");
        loadData(engine, stopwatch, "rangeU", uniform);

        LOG.info(" Loading to StorageBucketIntegers");
        loadData(engine, stopwatch, "bucketsU", uniform);

        LOG.info("Test 2. Gaussian distribution at [{}, {}], E = {}", -MAX_VAL, MAX_VAL, 0);
        LOG.info(" Loading to StorageRange");
        loadData(engine, stopwatch, "rangeG", gaussian);

        LOG.info(" Loading to StorageBucketIntegers");
        loadData(engine, stopwatch, "bucketsG", gaussian);


        Integer[] qUniform  = getQueryBounds(uniform);
        Integer[] qGaussian = getQueryBounds(gaussian);

        LOG.info("Test searching");
        LOG.info("Test 3. Uniform distribution at [{}, {}]", -MAX_VAL, MAX_VAL);
        LOG.info(" Searching in StorageRange");
        testQueries(engine, "rangeU", stopwatch, qUniform);

        LOG.info(" Searching in StorageBucketIntegers");
        testQueries(engine, "bucketsU", stopwatch, qUniform);

        LOG.info("Test 4. Gaussian distribution at [{}, {}], E = {}, σ^2 = {}", -MAX_VAL, MAX_VAL, 0);
        LOG.info(" Searching in StorageRange");
        testQueries(engine, "rangeG", stopwatch, qGaussian);

        LOG.info(" Searching in StorageBucketIntegers");
        testQueries(engine, "bucketsG", stopwatch, qGaussian);

    }

    private void testQueries(Engine engine, String attr, Stopwatch stopwatch, Integer[] queryData)
    {
        Query[] queries = generateQueries(attr, queryData);
        stopwatch.reset();
        for(int i = 0; i < QUERY_NUM; i++)
        {
            stopwatch.start();
            engine.executeQuery(queries[i]);
            stopwatch.stop();
        }

        LOG.info(" Searching time {} ms, average time per query {} ms",
                stopwatch.elapsed(TimeUnit.MILLISECONDS),
                stopwatch.elapsed(TimeUnit.MILLISECONDS) * 1.0 / QUERY_NUM);
    }

    private Query[] generateQueries(String attr, Integer[] qData)
    {
        Query[] queries = new Query[QUERY_NUM];
        for(int i = 0; i < QUERY_NUM; i++)
        {
            queries[i] = Query.build().query(attr, new RangeRequest(qData[2 * i], qData[2*i + 1]));
        }
        return queries;
    }

    private Integer[] getQueryBounds(Integer[] data)
    {
        Integer[] qData = new Integer[QUERY_NUM * 2];
        for(int i = 0; i < QUERY_NUM; i++)
        {
            Integer low = RandomUtils.uniformRandomElement(data);
            Integer high = RandomUtils.uniformRandomElement(data);
            if(low > high) {
                Integer temp = low;
                low = high;
                high = temp;
            }
            qData[2 * i] = low;
            qData[2 * i + 1] = high;
        }

        return qData;
    }

    private void generateData(Integer[] uniform, Integer[] gaussian)
    {
        Integer[] values = new Integer[VAL_NUM];
        for(int i = 0; i < VAL_NUM; i++)
            values[i] = RandomUtils.getNumber(-MAX_VAL, MAX_VAL);

        for(int i = 0; i < DOC_NUM; i++)
        {
            gaussian[i] = RandomUtils.gaussianRandomElement(values, 0.5, 0.25);
            uniform[i] = RandomUtils.uniformRandomElement(values);
        }

    }

    private void loadData(Engine engine, Stopwatch stopwatch, String attr, Integer[] data) {
        stopwatch.reset();
        for(int i = 0; i < DOC_NUM; i++) {
            stopwatch.start();
            int docId = engine.createDocument();
            engine.setAttributeByDocId(docId, attr, data[i]);
            stopwatch.stop();
        }
        LOG.info(" Loading time {} ms, average time {} ms",
                stopwatch.elapsed(TimeUnit.MILLISECONDS),
                stopwatch.elapsed(TimeUnit.MILLISECONDS) * 1.0/ DOC_NUM);
    }

    public static void main(String[] args) {
        new IntStoragesComparsion().run();
    }
}
