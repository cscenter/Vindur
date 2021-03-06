package ru.csc.vindur.test.comparison;

import com.google.common.base.Stopwatch;
import org.slf4j.impl.SimpleLogger;
import org.slf4j.impl.SimpleLoggerFactory;
import ru.csc.vindur.Engine;
import ru.csc.vindur.Query;
import ru.csc.vindur.storage.RangeRequest;
import ru.csc.vindur.storage.StorageBucketIntegers;
import ru.csc.vindur.storage.StorageRange;
import ru.csc.vindur.storage.StorageRangeBase;
import ru.csc.vindur.test.utils.RandomUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Andrey Kokorev
 *         Created on 10.12.2014.
 */
public class IntStoragesComparsion
{
    private static final SimpleLogger LOG = (SimpleLogger)new SimpleLoggerFactory().getLogger("Logger");
    static final int DOC_NUM_MIN = 1_000_000;
    static final int DOC_NUM_MAX = 2_000_000;
    static final int DOC_NUM_WARMUP = 10_000;

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
        performTest(stopwatch, uniform, qUniform, new StorageRange<>(Integer.class));
        performTest(stopwatch, uniform, qUniform, new StorageBucketIntegers());
        performTest(stopwatch, uniform, qUniform, new StorageArray(Integer.class));

        LOG.info("Testing");
        for(int docNum = DOC_NUM_MIN; docNum <= DOC_NUM_MAX; docNum += 1_000_000)
        {
            gaussian = new Integer[docNum];
            uniform = new Integer[docNum];
            generateData(uniform, gaussian, docNum);
            qUniform  = getQueryBounds(uniform);
            qGaussian = getQueryBounds(gaussian);

            LOG.info("Testing with {} values", docNum);
            LOG.info("Test StorageRange");
            LOG.info("Uniform distribution");
            performTest(stopwatch, uniform, qUniform, new StorageRange<>(Integer.class));
            LOG.info("Gaussian distribution");
            performTest(stopwatch, gaussian, qGaussian, new StorageRange<>(Integer.class));

            LOG.info("");
            LOG.info("Test StorageBucketIntegers");
            LOG.info("Uniform distribution");
            performTest(stopwatch, uniform, qUniform, new StorageBucketIntegers());
            LOG.info("Gaussian distribution");
            performTest(stopwatch, gaussian, qGaussian, new StorageBucketIntegers());

            LOG.info("");
            LOG.info("Test StorageArray");
            LOG.info("Uniform distribution");
            performTest(stopwatch, uniform, qUniform, new StorageArray(Integer.class));
            LOG.info("Gaussian distribution");
            performTest(stopwatch, gaussian, qGaussian, new StorageArray(Integer.class));

        }
    }

    private void performTest(Stopwatch stopwatch, Integer[] data, Integer[] qData, StorageRangeBase storage)
    {
        stopwatch.reset();
        Engine engine = Engine.build().storage("attr", storage).init();
        loadData(engine, stopwatch, "attr", data, data.length);
        testQueries(engine, "attr", stopwatch, qData);
    }


    private void testQueries(Engine engine, String attr, Stopwatch stopwatch, Integer[] queryData)
    {
        Query[] queries = generateQueries(attr, queryData);
        stopwatch.reset();
        long result = 0;
        for(int i = 0; i < QUERY_NUM; i++)
        {
            stopwatch.start();
            List<Integer> r = engine.executeQuery(queries[i]);
            stopwatch.stop();
            result += r.size();
        }

        LOG.info(" Searching time {} ms, average time per query {} ms",
                stopwatch.elapsed(TimeUnit.NANOSECONDS) / 1_000_000,
                stopwatch.elapsed(TimeUnit.NANOSECONDS) * 1.0 / QUERY_NUM / 1_000_000.0);
        LOG.info(" Found {} results overall, {} average", result, result * 1.0 / QUERY_NUM);
    }

    private Query[] generateQueries(String attr, Integer[] qData)
    {
        Query[] queries = new Query[QUERY_NUM];
        for(int i = 0; i < QUERY_NUM; i++)
            queries[i] = Query.build().query(attr, new RangeRequest(qData[2 * i], qData[2*i + 1]));
        return queries;
    }

    private Integer[] getQueryBounds(Integer[] data)
    {
        Integer[] qData = new Integer[QUERY_NUM * 2];
        for(int i = 0; i < QUERY_NUM; i++)
        {
            Integer low = RandomUtils.uniformRandomElement(data);
            Integer high = RandomUtils.uniformRandomElement(data);
            qData[2 * i] = Math.min(low,high);
            qData[2 * i + 1] = Math.max(low,high);;
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
            engine.setValue(docId, attr, data[i]);
            stopwatch.stop();
        }
        LOG.info(" Loading time {} ms, average time {} ms",
                stopwatch.elapsed(TimeUnit.NANOSECONDS) / 1000_000,
                stopwatch.elapsed(TimeUnit.NANOSECONDS) * 1.0/ docNum / 1_000_000.0);
    }

    public static void main(String[] args) {
        new IntStoragesComparsion().run();
    }
}
