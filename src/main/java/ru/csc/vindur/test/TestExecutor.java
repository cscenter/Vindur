package ru.csc.vindur.test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.csc.vindur.Engine;
import ru.csc.vindur.Engine.EngineBuilder;
import ru.csc.vindur.Query;
import ru.csc.vindur.test.utils.RandomUtils;

import com.google.common.base.Stopwatch;

/**
 * Created by dph on 06.11.2014.
 */
public class TestExecutor {
    private static final Logger LOG = LoggerFactory
            .getLogger(TestExecutor.class);

    protected EngineBuilder engineBuilder;
    private Supplier<Map<String, List<Object>>> documentSupplier;
    private Supplier<Query> querySupplier;

    protected Engine engine;

    public TestExecutor(EngineBuilder engineBuilder) {
        this.engineBuilder = engineBuilder;
    }

    public void execute(int docNumber, int reqNumber) {
        RandomUtils.setSeed(0);
        Stopwatch timer = Stopwatch.createUnstarted();
        this.engine = engineBuilder.createEngine();

        // fill documents
        long attributesSetted = 0;
        attributesSetted = documentExec(docNumber, timer);
        LOG.info("{} documents with {} attributes values loaded", docNumber,
                attributesSetted);
        LOG.info("Loading time is {}, average time is {}ms", timer,
                timer.elapsed(TimeUnit.MILLISECONDS) / (double) docNumber);

        // run executors
        long reqSize;
        reqSize = queryExec(reqNumber, timer);
        LOG.info("{} query executed for time {} with records {}", reqNumber,
                timer.elapsed(TimeUnit.MILLISECONDS), reqSize);
        LOG.info("Average time per query is {}ms",
                timer.elapsed(TimeUnit.MILLISECONDS) / (double) reqNumber);
    }

    protected long documentExec(int docNumber, Stopwatch timer) {
        long attributesSetted = 0;
        for (long i = 0; i < docNumber; i++) {
            Map<String, List<Object>> document = documentSupplier.get();
            LOG.debug("Document generated: {}", document);
            timer.start();

            int docId = engine.createDocument();
            for (Map.Entry<String, List<Object>> attribute : document
                    .entrySet()) {
                attributesSetted += attribute.getValue().size();
                for (Object value : attribute.getValue()) {
                    engine.setAttributeByDocId(docId, attribute.getKey(), value);
                }
            }
            timer.stop();
        }
        return attributesSetted;
    }

    protected Integer queryExec(int reqNumber, Stopwatch timer) {
        int size = 0;
        timer.reset();
        for (long i = 0; i < reqNumber; i++) {
            Query query = querySupplier.get();
            LOG.debug(" {}", query);
            timer.start();
            try {
                size += engine.executeQuery(query).size();
            } catch (Exception e) {
                LOG.error("Engine throw an exception: {}", e);
            } // даже не проверяем результат, только скорость
            timer.stop();
        }
        return size;
    }

    public TestExecutor setDocumentSupplier(
            Supplier<Map<String, List<Object>>> documentSupplier) {
        this.documentSupplier = documentSupplier;
        return this;
    }

    public TestExecutor setQuerySupplier(Supplier<Query> querySupplier) {
        this.querySupplier = querySupplier;
        return this;
    }

}
