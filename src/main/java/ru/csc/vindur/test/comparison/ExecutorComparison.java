package ru.csc.vindur.test.comparison;


import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.csc.vindur.Query;
import ru.csc.vindur.executor.DumbExecutor;
import ru.csc.vindur.executor.SmartExecutor;
import ru.csc.vindur.executor.TinyExecutor;
import ru.csc.vindur.storage.StorageRange;
import ru.csc.vindur.storage.StorageType;
import ru.csc.vindur.test.SimpleTest;
import ru.csc.vindur.test.TestBuilder;
import ru.csc.vindur.test.TestExecutor;
import ru.csc.vindur.test.TunableTestBuilder;
import ru.csc.vindur.test.utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Created by Edgar_Work on 16.12.2014.
 */
public class ExecutorComparison
{
    private static final Logger LOG = LoggerFactory
            .getLogger(TestExecutor.class);
    private static final int DOC_NUM = 200_000;
    private static final int QUERY_NUM = 10_000;
    private static final int QUERY_PARTS = 5;

    public void run()
    {
        TunableTestBuilder test;
        TestExecutor te;

        LOG.info("Warm up");
        test = TunableTestBuilder.build()
                .storage("S1", StorageType.STRING, 2000, 1.0) //category
                .storage("S2", StorageType.STRING, DOC_NUM, 1.0) //id
                .storage("S3", StorageType.STRING, 2, 1.0) //sex
                .storage("S4", StorageType.STRING, 10, 1.0) //age
                .storage("S5", StorageType.STRING, 10000, 0.5) //producer
                .storage("S6", StorageType.STRING, 100, 0.2) //?
                .storage("I1", StorageType.RANGE_STRING, 1000, 1.0) //price
                .storage("I2", StorageType.RANGE_INTEGER, 10000, 0.6) //weight
         .init();

        te = new TestExecutor(test.buildEngine(new DumbExecutor()));
        te.setDocumentSupplier(SimpleTest.docSupplier(test));
        te.setQuerySupplier(querySupplier(test, QUERY_PARTS));
        te.execute(DOC_NUM, QUERY_NUM);

        stqueries.clear();

        LOG.info("DumbExecutot test");
        test = TunableTestBuilder.build()
                .storage("S1", StorageType.STRING, 2000, 1.0) //category
//                .storage("S2", StorageType.STRING, DOC_NUM, 1.0) //id
                .storage("S3", StorageType.STRING, 2, 1.0) //sex
                .storage("S4", StorageType.STRING, 10, 1.0) //age
//                .storage("S5", StorageType.STRING, 10000, 0.5) //producer
//                .storage("S6", StorageType.STRING, 100, 0.2) //?
                .storage("I1", StorageType.RANGE_STRING, 1000, 1.0) //price
                .storage("I2", StorageType.RANGE_INTEGER, 10000, 0.6) //weight
                .init();
        te = new TestExecutor(test.buildEngine(new DumbExecutor()));
        te.setDocumentSupplier(SimpleTest.docSupplier(test));
        te.setQuerySupplier(querySupplier(test, QUERY_PARTS));
        te.execute(DOC_NUM, QUERY_NUM);

        AtomicInteger c1=new AtomicInteger(0);
        LOG.info("TinyExecutor test");
//        test = TunableTestBuilder.build()
//                .storage("S1", StorageType.STRING, 2000, 1.0) //category
//                .storage("S2", StorageType.STRING, DOC_NUM, 1.0) //id
//                .storage("S3", StorageType.STRING, 2, 1.0) //sex
//                .storage("S4", StorageType.STRING, 10, 1.0) //age
//                .storage("S5", StorageType.STRING, 10000, 0.5) //producer
//                .storage("S6", StorageType.STRING, 100, 0.2) //?
//                .storage("I1", StorageType.RANGE_STRING, 1000, 1.0) //price
//                .storage("I2", StorageType.RANGE_INTEGER, 10000, 0.6) //weight
//                .init();
        te = new TestExecutor(test.buildEngine(new TinyExecutor()));
        te.setDocumentSupplier(SimpleTest.docSupplier(test));
        te.setQuerySupplier(queryReplyer(c1));
        te.execute(DOC_NUM, QUERY_NUM);

        AtomicInteger c2=new AtomicInteger(0);
        LOG.info("SmartExecutor test");
//        test = TunableTestBuilder.build()
//                .storage("S1", StorageType.STRING, 2000, 1.0) //category
//                .storage("S2", StorageType.STRING, DOC_NUM, 1.0) //id
//                .storage("S3", StorageType.STRING, 2, 1.0) //sex
//                .storage("S4", StorageType.STRING, 10, 1.0) //age
//                .storage("S5", StorageType.STRING, 10000, 0.5) //producer
//                .storage("S6", StorageType.STRING, 100, 0.2) //?
//                .storage("I1", StorageType.RANGE_STRING, 1000, 1.0) //price
//                .storage("I2", StorageType.RANGE_INTEGER, 10000, 0.6) //weight
//                .init();
        te = new TestExecutor(test.buildEngine(new SmartExecutor(3)));
        te.setDocumentSupplier(SimpleTest.docSupplier(test));
        te.setQuerySupplier(queryReplyer(c2));
        te.execute(DOC_NUM, QUERY_NUM);



        /*LOG.info("Complex/EWH test");
        test = TunableTestBuilder.build()
                .storage("S1", StorageType.STRING, 2000, 1.0) //category
                .storage("S2", StorageType.STRING, DOC_NUM, 1.0) //id
                .storage("S3", StorageType.STRING, 2, 1.0) //sex
                .storage("S4", StorageType.STRING, 10, 1.0) //age
                .storage("S5", StorageType.STRING, 10000, 0.5) //producer
                .storage("S6", StorageType.STRING, 100, 0.2) //?
                .storage("S7", StorageType.STRING, 1000, 0.01)
                .storage("S8", StorageType.STRING, 1000, 0.001)
                .storage("I1", StorageType.RANGE_STRING, 1000, 1.0) //price
                .storage("I2", StorageType.RANGE_INTEGER, 10000, 0.6) //weight
                .storage("L1", StorageType.LUCENE_STRING, DOC_NUM, 0.8) //desc
                .init();

        Engine en = test.buildEngine(new TinyExecutor());
        te = new TestExecutor(en);
        te.setDocumentSupplier(SimpleTest.docSupplier(test));
        te.setQuerySupplier(querySupplier(test, QUERY_PARTS));
        te.execute(DOC_NUM, QUERY_NUM);*/
    }


    static List<Query> stqueries = new ArrayList<>();

    static Supplier<Query> querySupplier(final TestBuilder test,
                                         int partsInQuery) {
        return () -> {
            Query query = Query.build();
            for (String attr : RandomUtils.getRandomStrings(test.getStorages(),
                    partsInQuery))
            {
                StorageType type = test.getTypes().get(attr);
                Object val=null;
                switch (type)
                {
                    case STRING:
                    case INTEGER:
                        val =  RandomUtils.gaussianRandomElement(
                                test.getValues(attr), 0.5, 1.0 / 6);
                        break;
                    case RANGE_INTEGER:
                    case RANGE_STRING:
                        Object v1 = RandomUtils.gaussianRandomElement(test.getValues(attr), 0.5, 1.0 / 6);
                        Object v2 = RandomUtils.gaussianRandomElement(test.getValues(attr), 0.5, 1.0 / 6);
                        val = StorageRange.range(v1, v2);
                        break;
                    case LUCENE_STRING:
                        Object o = RandomUtils.gaussianRandomElement(test.getValues(attr), 0.5, 1.0 / 6);
                        try
                        {
                            val = new QueryParser("text", new WhitespaceAnalyzer()).parse(o.toString());
                        } catch (ParseException e)
                        {
                            e.printStackTrace();
                        }
                        break;
                }
                query.query(attr, val);
            }
            stqueries.add(query);
            return query;
        };
    }


    static Supplier<Query> queryReplyer(AtomicInteger counter)
    {
        return () ->
            stqueries.get(counter.getAndIncrement());

    };



    public static void main(String[] args) {
        new ExecutorComparison().run();
    }
}
