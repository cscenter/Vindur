package ru.csc.vindur.test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.csc.vindur.Engine;
import ru.csc.vindur.EngineConfig;
import ru.csc.vindur.Request;
import ru.csc.vindur.test.utils.RandomUtils;

import com.google.common.base.Stopwatch;

/**
 * Created by dph on 06.11.2014.
 */
public class TestExecutor {
    private static final Logger LOG = LoggerFactory
            .getLogger(TestExecutor.class);

    protected EngineConfig engineConfig;
    private Supplier<Map<String, List<Object>>> documentSupplier;
    private Supplier<Request> requestSupplier;

    protected Engine engine;

    public TestExecutor(EngineConfig engineConfig) {
        this.engineConfig = engineConfig;
    }

    public void execute(int docNumber, int reqNumber) {
        RandomUtils.setSeed(0);
        Stopwatch timer = Stopwatch.createUnstarted();
        this.engine = new Engine(engineConfig);

        // fill documents
        long attributesSetted = 0;
        attributesSetted = documentExec(docNumber, timer);
        LOG.info("{} documents with {} attributes values loaded", docNumber,
                attributesSetted);
        LOG.info("Loading time is {}, average time is {}ms", timer,
                timer.elapsed(TimeUnit.MILLISECONDS) / (double) docNumber);

        // run executors
        long reqSize;
        reqSize = requestExec(reqNumber, timer);
        LOG.info("{} request executed for time {} with records {}", reqNumber,
                timer.elapsed(TimeUnit.MILLISECONDS), reqSize);
        LOG.info("Average time per request is {}ms",
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

    protected Integer requestExec(int reqNumber, Stopwatch timer) {
        int size = 0;
        timer.reset();
        for (long i = 0; i < reqNumber; i++) {
            Request request = requestSupplier.get();
            LOG.debug(" {}", request);
            timer.start();
            try {
                size += engine.executeRequest(request).size();
            } catch (Exception e) {
                LOG.error("Engine throw an exception: {}", e);
                LOG.error("Stack trace: \n");
                e.printStackTrace();
                return 0;
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

    public TestExecutor setRequestSupplier(Supplier<Request> requestSupplier) {
        this.requestSupplier = requestSupplier;
        return this;
    }

}
