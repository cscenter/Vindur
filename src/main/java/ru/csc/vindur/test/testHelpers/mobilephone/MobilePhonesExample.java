package ru.csc.vindur.test.testHelpers.mobilephone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.csc.vindur.EngineConfig;
import ru.csc.vindur.Request;
import ru.csc.vindur.bitset.bitsetFabric.EWAHBitSetFabric;
import ru.csc.vindur.document.StorageType;
import ru.csc.vindur.document.Value;
import ru.csc.vindur.optimizer.TinyOptimizer;
import ru.csc.vindur.test.utils.RandomUtils;
import ru.csc.vindur.test2.MultiThreadTestExecutor;
import ru.csc.vindur.test2.SimpleTestBuilder;
import ru.csc.vindur.test2.TestBuilder;
import ru.csc.vindur.test2.TestExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Pavel Chursin on 17.11.2014.
 */
public class MobilePhonesExample
{
        private static final Logger LOG = LoggerFactory.getLogger(MobilePhoneTest.class);

        public static void main(String[] args)
        {
            MobilePhoneTestBuilder test;
            TestExecutor te;

            System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
            System.setProperty("org.slf4j.simpleLogger.log.ru.csc", "info");

            test = new MobilePhoneTestBuilder();
            te = new TestExecutor(new EngineConfig(test.getTypes(), new EWAHBitSetFabric(), new TinyOptimizer()));
            te.setDocumentSupplier( docSupplier(test) );
            te.setRequestSupplier( requestSupplier(test,5) );
            te.execute(100000, 0);

        }


        private static Supplier<Request> requestSupplier(final MobilePhoneTestBuilder test, int partInRequest)
        {
            return () ->
            {
                Request request = Request.build();
//                for (String attr : RandomUtils.getRandomStrings(test.getStorages(), partInRequest))
//                {
//                        Value val = RandomUtils.gaussianRandomElement(test.getValues(attr), 0.5, 1.0 / 6);
//                        request.exact(attr, val.getValue());
//                    }
                return request;
            };
        }

        private static Supplier<Map<String,List<Value>>> docSupplier(final MobilePhoneTestBuilder test)
        {
            return () -> test.getDocument();
        }

}
