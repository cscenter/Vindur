package ru.csc.vindur.test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.csc.vindur.Engine;
import ru.csc.vindur.Query;
import ru.csc.vindur.bitset.EWAHBitArray;
import ru.csc.vindur.executor.DumbExecutor;
import ru.csc.vindur.executor.SmartExecutor;
import ru.csc.vindur.storage.StorageType;
import ru.csc.vindur.test.utils.RandomUtils;

/**
 * Created by jetbrains on 06.11.2014.
 */
public class SimpleTest {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleTest.class);

    public static void main(String[] args) {
        SimpleTestBuilder test;
        TestExecutor te;
        Engine e;

        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
        System.setProperty("org.slf4j.simpleLogger.log.ru.csc", "info");

        // warm stage
        test = SimpleTestBuilder.build(20)
                .setTypeFrequence(StorageType.STRING, 0.8)
                .setTypeFrequence(StorageType.INTEGER, 0.2)
                .setValuesCount(StorageType.STRING, 30)
                .setValuesCount(StorageType.INTEGER, 30).init();
        te = new TestExecutor(test.buildEngine(new SmartExecutor(5000)));
        te.setDocumentSupplier(docSupplier(test));
        te.setQuerySupplier(querySupplier(test, 5));
        te.execute(100000, 100000);

        LOG.info("STRING/EWH test");
        test = SimpleTestBuilder.build(1)
                .setTypeFrequence(StorageType.STRING, 1.0)
                .setValuesCount(StorageType.STRING, 30000).init();
        te = new TestExecutor(test.buildEngine(new SmartExecutor(5000)));
        te.setDocumentSupplier(docSupplier(test));
        te.setQuerySupplier(querySupplier(test, 1));
        te.execute(1000000, 100000);

        LOG.info("NUMERIC/EWH test");
        test = SimpleTestBuilder.build(1)
                .setTypeFrequence(StorageType.INTEGER, 1.0)
                .setValuesCount(StorageType.INTEGER, 3000).init();
        te = new TestExecutor(test.buildEngine(new SmartExecutor(5000)));
        te.setDocumentSupplier(docSupplier(test));
        te.setQuerySupplier(querySupplier(test, 1));
        te.execute(100000, 100000);

        LOG.info("Complex/EWH test");
        test = SimpleTestBuilder.build(20)
                .setTypeFrequence(StorageType.STRING, 0.8)
                .setTypeFrequence(StorageType.INTEGER, 0.2)
                .setValuesCount(StorageType.STRING, 30)
                .setValuesCount(StorageType.INTEGER, 30).init();
        te = new TestExecutor(test.buildEngine(new SmartExecutor(5000)));
        te.setDocumentSupplier(docSupplier(test));
        te.setQuerySupplier(querySupplier(test, 5));
        te.execute(100000, 100000);

    }

    static Supplier<Query> querySupplier(final TestBuilder test,
                                                 int partsInQuery) {
        return () -> {
            Query query = Query.build();
            for (String attr : RandomUtils.getRandomStrings(test.getStorages(),
                    partsInQuery)) {
                Object val = RandomUtils.gaussianRandomElement(
                        test.getValues(attr), 0.5, 1.0 / 6);
                query.query(attr, val);
            }
            return query;
        };
    }

    public static Supplier<Map<String, List<Object>>> docSupplier(
            final TestBuilder test) {
        Random random = new Random();
        return () -> test
                .getStorages()
                .stream()
                .filter(attr -> random.nextDouble() < test.getProbability(attr))
                // не каждый атрибут в этом документе
                .collect(
                        Collectors.toMap((String e) -> e, e -> Arrays
                                .asList(RandomUtils.gaussianRandomElement(
                                        test.getValues(e), 0.5, 1.0 / 6))));
    }

}
