package ru.csc.vindur.test2;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.csc.vindur.Engine;
import ru.csc.vindur.EngineConfig;
import ru.csc.vindur.Request;
import ru.csc.vindur.document.Value;
import ru.csc.vindur.test.utils.RandomUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Created by dph on 06.11.2014.
 */
public class TestExecutor
{
    private static final Logger LOG = LoggerFactory.getLogger(TestExecutor.class);

    private EngineConfig engineConfig;
    private Supplier<Map<String,List<Value>>> documentSupplier;
    private Supplier<Request> requestSupplier;

    private Engine engine;

    public TestExecutor(EngineConfig engineConfig)
    {
        this.engineConfig = engineConfig;
    }

    public void execute(int docNumber, int reqNumber)
    {
        RandomUtils.setSeed(0);
        Stopwatch timer = Stopwatch.createUnstarted();
        this.engine = new Engine(engineConfig);

        //fill documents
        long attributesSetted = 0;
        attributesSetted = documentExec(docNumber,timer);
        LOG.info("{} documents with {} attributes values loaded", docNumber, attributesSetted);
        LOG.info("Loading time is {}, average time is {}ms", timer, timer.elapsed(TimeUnit.MILLISECONDS) / (double)docNumber );

        //run executors
        requestExec(reqNumber,timer);
        LOG.info("{} request executed for time {}", reqNumber, timer.elapsed(TimeUnit.MILLISECONDS));
        LOG.info("Average time per request is {}ms", timer.elapsed(TimeUnit.MILLISECONDS) / (double)reqNumber);
    }

    protected long documentExec(int docNumber, Stopwatch timer)
    {
        long attributesSetted = 0;
        for (long i=0; i<docNumber; i++)
        {
            Map<String,List<Value>> document = documentSupplier.get();
            LOG.debug("Document generated: {}", document);
            timer.start();

            int docId = engine.createDocument();
            for(Map.Entry<String, List<Value>> attribute: document.entrySet())
            {
                attributesSetted += attribute.getValue().size();
                for(Value value: attribute.getValue())
                    engine.setAttributeByDocId(docId, attribute.getKey(), value);
            }
            timer.stop();
        }
        return attributesSetted;
    }

    protected void requestExec(int reqNumber, Stopwatch timer)
    {
        timer.reset();
        for (long i=0; i<reqNumber; i++)
        {
            Request request = requestSupplier.get();
            LOG.debug(" {}", request);
            timer.start();
            engine.executeRequest(request); //даже не проверяем результат, только скорость
            timer.stop();
        }
    }

    public TestExecutor setDocumentSupplier(Supplier<Map<String, List<Value>>> documentSupplier) {
        this.documentSupplier = documentSupplier;
        return this;
    }

    public TestExecutor setRequestSupplier(Supplier<Request> requestSupplier)
    {
        this.requestSupplier = requestSupplier;
        return this;
    }

}
