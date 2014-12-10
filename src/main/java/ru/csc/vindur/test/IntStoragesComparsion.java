package ru.csc.vindur.test;

import com.google.common.base.Stopwatch;
import org.slf4j.impl.SimpleLogger;
import org.slf4j.impl.SimpleLoggerFactory;
import ru.csc.vindur.Engine;
import ru.csc.vindur.Query;
import ru.csc.vindur.storage.RangeRequest;
import ru.csc.vindur.storage.StorageBucketIntegers;
import ru.csc.vindur.storage.StorageRange;
import ru.csc.vindur.test.utils.RandomUtils;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author Andrey Kokorev
 *         Created on 10.12.2014.
 */
public class IntStoragesComparsion
{
    private static final SimpleLogger LOG = (SimpleLogger)new SimpleLoggerFactory().getLogger("Logger");
    static final int DOC_NUM_MIN = 10_000;
    static final int DOC_NUM_MAX = 10_000_000;
    static final int DOC_NUM_WARMUP = 10_000;

    static final int BUCKET_SIZE_MIN  = 100;
    static final int BUCKET_SIZE_MAX  = 5000;
    static final int BUCKET_SIZE_STEP = 200;

    static final int MAX_VAL = 10_000;

    static final int VAL_NUM = 2 * MAX_VAL;
    static final int QUERY_NUM = 10_000;
    public void run()
    {
        Stopwatch stopwatch = Stopwatch.createUnstarted();
        Integer[] gaussian = new Integer[DOC_NUM_WARMUP];
        Integer[] uniform = new Integer[DOC_NUM_WARMUP];
        generateData(uniform, gaussian, DOC_NUM_WARMUP);
        Integer[] qUniform  = getQueryBounds(uniform);
        Integer[] qGaussian;

        LOG.info("Warmup");
        warmUp(uniform, qUniform);

        LOG.info("Testing");
        for(int docNum = DOC_NUM_MIN; docNum <= DOC_NUM_MAX; docNum *= 10)
        {
            gaussian = new Integer[docNum];
            uniform = new Integer[docNum];
            generateData(uniform, gaussian, docNum);
            qUniform  = getQueryBounds(uniform);
            qGaussian = getQueryBounds(gaussian);

            Engine engineRange = Engine.build()
                    .storage("rangeU", new StorageRange<>(Integer.class))
                    .storage("rangeG", new StorageRange<>(Integer.class))
                    .init();

            LOG.info("");
            LOG.info("Testing with {} values", docNum);
            LOG.info("");
            LOG.info("Test StorageRange");
            LOG.info(" Loading uniform distribution at [{}, {}]", -MAX_VAL, MAX_VAL);
            loadData(engineRange, stopwatch, "rangeU", uniform, docNum);
            LOG.info(" Loading Gaussian distribution at [{}, {}], E = {}", -MAX_VAL, MAX_VAL, 0);
            loadData(engineRange, stopwatch, "rangeG", gaussian, docNum);
            LOG.info(" Searching uniform distributed data");
            testQueries(engineRange, "rangeU", stopwatch, qUniform);
            LOG.info(" Searching Gaussian distributed data");
            testQueries(engineRange, "rangeG", stopwatch, qGaussian);
            LOG.info("--");
            for(int bucketSize = BUCKET_SIZE_MIN; bucketSize <= BUCKET_SIZE_MAX; bucketSize += BUCKET_SIZE_STEP)
            {
                Engine engineBucket = Engine.build()
                        .storage("bucketsU", new StorageBucketIntegers(bucketSize))
                        .storage("bucketsG", new StorageBucketIntegers(bucketSize))
                        .init();
                LOG.info("Test StorageBucket, bucketSize = {}", bucketSize);
                LOG.info(" Loading uniform distribution at [{}, {}]", -MAX_VAL, MAX_VAL);
                loadData(engineBucket, stopwatch, "bucketsU", uniform, docNum);
                LOG.info(" Loading Gaussian distribution at [{}, {}]", -MAX_VAL, MAX_VAL, 0);
                loadData(engineBucket, stopwatch, "bucketsG", gaussian, docNum);
                LOG.info(" Searching uniform distributed data");
                testQueries(engineBucket, "bucketsU", stopwatch, qUniform);
                LOG.info(" Searching Gaussian distributed data");
                testQueries(engineBucket, "bucketsG", stopwatch, qGaussian);
                LOG.info("--");
            }
            LOG.info("==");
        }
    }

    private void warmUp(Integer[] data, Integer[] qData)
    {
        Engine engine = Engine.build()
                .storage("warmupRange", new StorageRange<>(Integer.class))
                .storage("warmupBuckets", new StorageBucketIntegers()).init();
        for(int i = 0; i < data.length; i++)
        {
            int docId = engine.createDocument();
            engine.setAttributeByDocId(docId, "warmupRange", data[i]);
            engine.setAttributeByDocId(docId, "warmupBuckets", data[i]);
        }

        Query[] queriesR = generateQueries("warmupRange", qData);
        Query[] queriesB = generateQueries("warmupBuckets", qData);
        for(int i = 0; i < queriesB.length; i++)
        {
            engine.executeQuery(queriesR[i]);
            engine.executeQuery(queriesB[i]);
        }
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

    private void generateData(Integer[] uniform, Integer[] gaussian, int docNum)
    {
        Integer[] values = new Integer[VAL_NUM];
        for(int i = 0; i < VAL_NUM; i++)
            values[i] = RandomUtils.getNumber(-MAX_VAL, MAX_VAL);
        Arrays.sort(values);

        for(int i = 0; i < docNum; i++)
        {
            gaussian[i] = RandomUtils.gaussianRandomElement(values, 0.5, 0.25);
            uniform[i] = RandomUtils.uniformRandomElement(values);
        }

    }

    private void loadData(Engine engine, Stopwatch stopwatch, String attr, Integer[] data, int docNum) {
        stopwatch.reset();
        for(int i = 0; i < docNum; i++) {
            stopwatch.start();
            int docId = engine.createDocument();
            engine.setAttributeByDocId(docId, attr, data[i]);
            stopwatch.stop();
        }
        LOG.info(" Loading time {} ms, average time {} ms",
                stopwatch.elapsed(TimeUnit.MILLISECONDS),
                stopwatch.elapsed(TimeUnit.MILLISECONDS) * 1.0/ docNum);
    }

    public static void main(String[] args) {
        new IntStoragesComparsion().run();
    }
}
