package ru.csc.vindur.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.csc.vindur.EngineConfig;
import ru.csc.vindur.Request;
import ru.csc.vindur.bitset.EWAHBitSet;
import ru.csc.vindur.optimizer.DumbOptimizer;
import ru.csc.vindur.storage.StorageType;
import ru.csc.vindur.test.utils.RandomUtils;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by jetbrains on 06.11.2014.
 */
public class SimpleTest
{
    private static final Logger LOG = LoggerFactory.getLogger(SimpleTest.class);

    public static void main(String[] args)
    {
        SimpleTestBuilder test;
        TestExecutor te;

        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
        System.setProperty("org.slf4j.simpleLogger.log.ru.csc", "info");

        //warm stage
        test = SimpleTestBuilder.build(20)
                .setTypeFrequence(StorageType.STRING, 0.8)
                .setTypeFrequence(StorageType.INTEGER, 0.2)
                .setValuesCount(StorageType.STRING, 30)
                .setValuesCount(StorageType.INTEGER, 30)
                .init();
        te = new TestExecutor(new EngineConfig(test.getTypes(), EWAHBitSet::new, new DumbOptimizer()));
        te.setDocumentSupplier(docSupplier(test));
        te.setRequestSupplier(requestSupplier(test, 5));
        te.execute(100000, 100000);

        LOG.info("STRING/EWH test");
        test = SimpleTestBuilder.build(1)
                .setTypeFrequence(StorageType.STRING, 1.0)
                .setValuesCount(StorageType.STRING, 30000)
                .init();
        te = new TestExecutor(new EngineConfig(test.getTypes(), EWAHBitSet::new, new DumbOptimizer()));
        te.setDocumentSupplier(docSupplier(test));
        te.setRequestSupplier(requestSupplier(test, 1));
        te.execute(1000000, 100000);

        LOG.info("NUMERIC/EWH test");
        test = SimpleTestBuilder.build(1)
                .setTypeFrequence(StorageType.INTEGER, 1.0)
                .setValuesCount(StorageType.INTEGER, 3000)
                .init();
        te = new TestExecutor(new EngineConfig(test.getTypes(), EWAHBitSet::new, new DumbOptimizer()));
        te.setDocumentSupplier(docSupplier(test));
        te.setRequestSupplier(requestSupplier(test, 1));
        te.execute(100000, 100000);

        LOG.info("Complex/EWH test");
        test = SimpleTestBuilder.build(20)
                .setTypeFrequence(StorageType.STRING, 0.8)
                .setTypeFrequence(StorageType.INTEGER, 0.2)
                .setValuesCount(StorageType.STRING, 30)
                .setValuesCount(StorageType.INTEGER, 30)
                .init();
        te = new TestExecutor(new EngineConfig(test.getTypes(), EWAHBitSet::new, new DumbOptimizer()));
        te.setDocumentSupplier(docSupplier(test));
        te.setRequestSupplier(requestSupplier(test, 5));
        te.execute(100000, 100000);

    }


    private static Supplier<Request> requestSupplier(final TestBuilder test, int partInRequest)
    {
        return () ->
        {
            Request request = Request.build();
            for (String attr : RandomUtils.getRandomStrings(test.getStorages(), partInRequest))
            {
                	Object val = RandomUtils.gaussianRandomElement(test.getValues(attr), 0.5, 1.0 / 6);
                    request.request(attr, val);
            }
            return request;
        };
    }

    private static Supplier<Map<String, List<Object>>> docSupplier(final TestBuilder test)
    {
        Random random = new Random();
        return () ->
                test.getStorages().stream()
                        .filter(attr -> random.nextDouble() < test.getProbability(attr)) // не каждый атрибут в этом документе
                        .collect(Collectors.toMap
                                        (e -> e,
                                                e -> Arrays.asList(RandomUtils.gaussianRandomElement(test.getValues(e), 0.5, 1.0 / 6))
                                        )
                        );
    }
}
