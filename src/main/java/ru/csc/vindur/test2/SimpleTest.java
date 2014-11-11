package ru.csc.vindur.test2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.csc.vindur.EngineConfig;
import ru.csc.vindur.Request;
import ru.csc.vindur.bitset.bitsetFabric.EWAHBitSetFabric;
import ru.csc.vindur.document.StorageType;
import ru.csc.vindur.document.Value;
import ru.csc.vindur.optimizer.TinyOptimizer;
import ru.csc.vindur.test.utils.RandomUtils;

import java.util.*;
import java.util.concurrent.Executors;
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
                .setTypeFrequence(StorageType.STRING, 0.4)
                .setTypeFrequence(StorageType.ENUM, 0.4)
                .setTypeFrequence(StorageType.NUMERIC, 0.2)
                .setValuesCount(StorageType.ENUM, 5)
                .setValuesCount(StorageType.STRING, 30)
                .setValuesCount(StorageType.NUMERIC, 30)
                .init();
        te = new TestExecutor(new EngineConfig(test.getTypes(), new EWAHBitSetFabric(), Executors.newSingleThreadExecutor(), new TinyOptimizer()));
        te.setDocumentSupplier( docSupplier(test) );
        te.setRequestSupplier( requestSupplier(test,5) );
        te.execute(100000, 100000);
       //

       System.setProperty("org.slf4j.simpleLogger.log.ru.csc", "info");
       LOG.info("ENUM/EWH test");
       test = SimpleTestBuilder.build(1)
            .setTypeFrequence(StorageType.ENUM,1.0)
            .setValuesCount(StorageType.ENUM,100)
            .init();
        te = new TestExecutor(new EngineConfig(test.getTypes(), new EWAHBitSetFabric(), Executors.newFixedThreadPool(4),new TinyOptimizer()));
         te.setDocumentSupplier( docSupplier(test) );
         te.setRequestSupplier( requestSupplier(test,1) );
        te.execute(1000000, 10000);

        LOG.info("STRING/EWH test");
        test = SimpleTestBuilder.build(1)
                .setTypeFrequence(StorageType.STRING,1.0)
                .setValuesCount(StorageType.STRING,30000)
                .init();
        te = new TestExecutor(new EngineConfig(test.getTypes(), new EWAHBitSetFabric(), Executors.newFixedThreadPool(4),new TinyOptimizer()));
         te.setDocumentSupplier( docSupplier(test) );
         te.setRequestSupplier( requestSupplier(test,1) );
        te.execute(1000000,100000);

//        for(int i = 0; i < 100; i++) {
//            LOG.info("NUMERIC/EWH test");
//            test = SimpleTestBuilder.build(1)
//                    .setTypeFrequence(StorageType.NUMERIC, 1.0)
//                    .setValuesCount(StorageType.NUMERIC, 3000)
//                    .init();
//            te = new TestExecutor(new EngineConfig(test.getTypes(), new EWAHBitSetFabric(), Executors.newFixedThreadPool(4), new TinyOptimizer()));
//            te.setDocumentSupplier(docSupplier(test));
//            te.setRequestSupplier(requestSupplier(test, 1));
//            te.execute(100000, 100000);
//        }

        LOG.info("Complex/EWH test");
        test = SimpleTestBuilder.build(20)
          .setTypeFrequence(StorageType.STRING, 0.4)
          .setTypeFrequence(StorageType.ENUM, 0.4)
          .setTypeFrequence(StorageType.NUMERIC, 0.2)
          .setValuesCount(StorageType.ENUM, 5)
          .setValuesCount(StorageType.STRING, 30)
          .setValuesCount(StorageType.NUMERIC, 30)
          .init();
        te = new TestExecutor(new EngineConfig(test.getTypes(), new EWAHBitSetFabric(), Executors.newFixedThreadPool(4), new TinyOptimizer()));
        te.setDocumentSupplier( docSupplier(test) );
        te.setRequestSupplier( requestSupplier(test,5) );
        te.execute(100000, 100000);

        LOG.info("Complex/EWH test in 4 threads");
        te = new MultiThreadTestExecutor(new EngineConfig(test.getTypes(), new EWAHBitSetFabric(), Executors.newSingleThreadExecutor(), new TinyOptimizer()),4);
        te.setDocumentSupplier( docSupplier(test) );
        te.setRequestSupplier( requestSupplier(test,5) );
        te.execute(100000, 100000);

    }


    private static Supplier<Request> requestSupplier(final TestBuilder test, int partInRequest)
    {
       return () ->
       {
            Request request = Request.build();
            for (String attr : RandomUtils.getRandomStrings(test.getStorages(), partInRequest))
            {
//                if ( (test.getTypes().get(attr) == StorageType.NUMERIC) && (Math.random() > 0.5))
//                {
//                        Value val1 = RandomUtils.gaussianRandomElement(test.getValues(attr), 0.5, 1.0 / 6);
//                        Value val2 = RandomUtils.gaussianRandomElement(test.getValues(attr), 0.5, 1.0 / 6);
//                        request.range(attr, val1.getValue(), val2.getValue());
//                }
//                else
                  {
                        Value val = RandomUtils.gaussianRandomElement(test.getValues(attr), 0.5, 1.0 / 6);
                        request.exact(attr, val.getValue());
                  }
            }
           return request;
       };
    }

    private static Supplier<Map<String,List<Value>>> docSupplier(final TestBuilder test)
    {
        Random random = new Random();
        return () ->
          test.getStorages().stream()
          .filter(attr -> random.nextDouble() < test.getProbability(attr) ) // не каждый атрибут в этом документе
          .collect(Collectors.toMap
                          (e -> e,
                           e -> list(RandomUtils.gaussianRandomElement(test.getValues(e), 0.5, 1.0 / 6))
                          )
          );
    }


    private static <T> List<T> list (T t)
    {
        List<T> l = new ArrayList<>(1);
        l.add(t);
        return l;
    }
}