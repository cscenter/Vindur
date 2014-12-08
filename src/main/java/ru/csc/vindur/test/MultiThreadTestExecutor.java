package ru.csc.vindur.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.csc.vindur.Engine;
import ru.csc.vindur.test.utils.RandomUtils;

import com.google.common.base.Stopwatch;

/**
 * Created by jetbrains on 07.11.2014.
 */
public class MultiThreadTestExecutor extends TestExecutor
{
    private static final Logger LOG = LoggerFactory.getLogger(MultiThreadTestExecutor.class);
    private int threadCount;

    public MultiThreadTestExecutor(Engine engine, int threadCount) {
        super(engine);
        this.threadCount = threadCount;
    }

    @Override
    public void execute(int docNumber, int reqNumber) {
        RandomUtils.setSeed(0);
        Stopwatch timer = Stopwatch.createUnstarted();
        ExecutorService service = Executors.newFixedThreadPool(threadCount);

        // fill documents
        long attributesSetted = 0;
        attributesSetted = documentExec(docNumber, timer);
        LOG.info("{} documents with {} attributes values loaded", docNumber,
                attributesSetted);
        LOG.info("Loading time is {}, average time is {}ms", timer,
                timer.elapsed(TimeUnit.MILLISECONDS) / (double) docNumber);

        // run executors
        List<Callable<Integer>> tasks = new ArrayList<>();
        final AtomicLong allTime = new AtomicLong(0);

        for (int i = 0; i < threadCount; i++) {
            tasks.add(() -> {
                Stopwatch ltimer = Stopwatch.createUnstarted();
                Integer result = super.queryExec(reqNumber / threadCount,
                        ltimer);
                allTime.addAndGet(ltimer.elapsed(TimeUnit.MICROSECONDS));
                return result;
            });
        }

        timer.reset();
        try {
            timer.start();
            service.invokeAll(tasks);
            timer.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        service.shutdown();
        LOG.info("{} query executed for time {} and in time {}", reqNumber,
                allTime.get() / 1000, timer.elapsed(TimeUnit.MILLISECONDS));
        LOG.info("Average time per query is {}ms", allTime.get()
                / (double) (reqNumber * 1000));

    }

}
