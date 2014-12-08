package ru.csc.vindur.example;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import ru.csc.vindur.Engine;
import ru.csc.vindur.Query;
import ru.csc.vindur.bitset.EWAHBitSet;
import ru.csc.vindur.executor.DumbExecutor;
import ru.csc.vindur.test.TestExecutor;

/**
 * Created by Pavel Chursin on 17.11.2014.
 */
public class MobilePhonesExample {
    public static void main(String[] args) {
        MobilePhoneTestBuilder test;
        TestExecutor te;

        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
        System.setProperty("org.slf4j.simpleLogger.log.ru.csc", "info");

        test = new MobilePhoneTestBuilder();
//        te = new TestExecutor(test.buildEngine(EWAHBitSet::new, new DumbExecutor()));
//        te.setDocumentSupplier(docSupplier(test));
//        te.setQuerySupplier(requestSupplier(test, 5));
//        te.execute(100000, 0);

    }

    private static Supplier<Query> requestSupplier(
            final MobilePhoneTestBuilder test, int partInRequest) {
        return () -> {
            Query query = test.getRandomAttributesRequest();
            return query;
        };
    }

    private static Supplier<Map<String, List<Object>>> docSupplier(
            final MobilePhoneTestBuilder test) {
        return () -> test.getDocument();
    }

}
