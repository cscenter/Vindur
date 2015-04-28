package ru.csc.vindur.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.csc.vindur.Engine;
import ru.csc.vindur.Query;
import ru.csc.vindur.executor.*;
import ru.csc.vindur.storage.StorageExact;

import java.util.List;

/**
 * Created by edgar on 26.03.15.
 */
public class TuningTest {

    private static final Logger LOG = LoggerFactory.getLogger(TuningTest.class);

    public static void main(String[] args) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
        System.setProperty("org.slf4j.simpleLogger.log.ru.csc", "info");

        StorageExact stringStorage = new StorageExact<>(String.class);
        StorageExact intStorage = new StorageExact<>(Integer.class);

        Engine engine = Engine.build()
                .executor(new TunableExecutor())
                .storage("Name", stringStorage)
                .storage("Age", intStorage)
                .init();

        int docID = engine.createDocument();
        engine.setValue(docID, "Name", "Edgar");
        engine.setValue(docID, "Age", 22);

        Query query = Query.build()
                .query("Name", "Edgar");

        List<Integer> res = engine.executeQuery(query);

        LOG.info(String.valueOf(res.contains(docID)));
    }
}
