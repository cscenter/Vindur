package ru.csc.vindur.test.testHelpers.mobilephone;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

import ru.csc.vindur.Engine;
import ru.csc.vindur.Request;
import ru.csc.vindur.bitset.bitsetFabric.BitSetFabric;
import ru.csc.vindur.bitset.bitsetFabric.EWAHBitSetFabric;
import ru.csc.vindur.document.Value;
import ru.csc.vindur.document.StorageType;
import ru.csc.vindur.test.DocumentGeneratorBase;
import ru.csc.vindur.test.RequestGeneratorBase;
import ru.csc.vindur.test.VindurEngineTest;
import ru.csc.vindur.test.testHelpers.MultyAttributesTest;
import ru.csc.vindur.test.testHelpers.OneAttributeTest;
import ru.csc.vindur.test.testHelpers.TestHelper;
import ru.csc.vindur.test.testHelpers.mobilephone.MobilePhoneTest;
import ru.csc.vindur.test.utils.RandomUtils;

public class MobilePhoneTestRunner {
    private static final Logger LOG = LoggerFactory.getLogger(VindurEngineTest.class);

    public static void main(String[] args) {
        BitSetFabric fabric = new EWAHBitSetFabric();
        run(new MobilePhoneTest(10000, 100, 2, fabric));
    }

    private static void run(TestHelper helper) {
        LOG.info("Test with\n{}\nstarted", helper);
        RandomUtils.setSeed(0);
        Engine engine = new Engine(helper.getEngineConfig());
        DocumentGeneratorBase documentGenerator = helper.getDocumentGenerator();

        Stopwatch loadingTime = Stopwatch.createStarted();
        long attributesSetted = 0;
        for (Map<String, List<Value>> document: documentGenerator) {
            LOG.debug("Document generated: {}", document);
            int docId = engine.createDocument();
            attributesSetted += loadDocument(engine, document, docId);
        }
        LOG.info("{} documents with {} atribute values loaded", documentGenerator.getDocumentsCount(),
                attributesSetted);
        LOG.info("Loading time is {}", loadingTime.stop());
        loadingTime = null;

        RequestGeneratorBase requestGenerator = helper.getRequestGenerator();

        Stopwatch executingTime = Stopwatch.createStarted();
        long resultsCount = 0;
        for (Request request: requestGenerator) {
            LOG.debug("Request generated: {}", request);
            List<Integer> result = engine.executeRequest(request);
            LOG.debug("Engine returned {} results", result.size());
            resultsCount += result.size();
        }
        LOG.info("{} request executed", requestGenerator.getRequestsCount());
        LOG.info("Executing time is {}. Engine returned {} results", executingTime.stop(), resultsCount);
    }

    private static long loadDocument(Engine engine, Map<String, List<Value>> document, int docId) {
        long settedAttributes = 0;
        for(Entry<String, List<Value>> attribute: document.entrySet()) {
            settedAttributes += attribute.getValue().size();
            for(Value value: attribute.getValue()) {
                engine.setAttributeByDocId(docId, attribute.getKey(), value);
            }
        }
        return settedAttributes;
    }

}
