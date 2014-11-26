package ru.csc.vindur.example;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import ru.csc.vindur.EngineConfig;
import ru.csc.vindur.Request;
import ru.csc.vindur.bitset.EWAHBitSet;
import ru.csc.vindur.optimizer.DumbOptimizer;
import ru.csc.vindur.test.TestExecutor;

/**
 * Created by Pavel Chursin on 17.11.2014.
 */
public class MobilePhonesExample
{
    public static void main(String[] args)
    {
        MobilePhoneTestBuilder test;
        TestExecutor te;

        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
        System.setProperty("org.slf4j.simpleLogger.log.ru.csc", "info");

        test = new MobilePhoneTestBuilder();
        te = new TestExecutor(new EngineConfig(test.getTypes(), EWAHBitSet::new, new DumbOptimizer()));
        te.setDocumentSupplier(docSupplier(test));
        te.setRequestSupplier(requestSupplier(test, 5));
        te.execute(100000, 0);

    }


    private static Supplier<Request> requestSupplier(final MobilePhoneTestBuilder test, int partInRequest)
    {
        return () ->
        {
            Request request = test.getRandomAttributesRequest();
            return request;
        };
    }

    private static Supplier<Map<String, List<Object>>> docSupplier(final MobilePhoneTestBuilder test)
    {
        return () -> test.getDocument();
    }

}
