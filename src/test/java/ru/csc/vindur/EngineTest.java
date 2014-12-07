package ru.csc.vindur;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import ru.csc.vindur.bitset.EWAHBitSet;
import ru.csc.vindur.storage.StorageLucene;
import ru.csc.vindur.storage.StorageRangeBase;
import ru.csc.vindur.storage.StorageType;

public class EngineTest {
    private static final String INT_ATTR = "Int";
    private static final String STR_ATTR = "Str";
    private static final String STR_ATTR2 = "SortedStr";
    private static final String STR_ATTR3 = "LuceneStr";

    @Test
    public void simpleTest() throws Exception {
        Map<String, StorageType> indexes = new HashMap<>();
        indexes.put(STR_ATTR, StorageType.STRING);
        indexes.put(INT_ATTR, StorageType.RANGE_INTEGER);
        indexes.put(STR_ATTR2, StorageType.RANGE_STRING);
        indexes.put(STR_ATTR3, StorageType.LUCENE_STRING);
        Engine engine = new Engine.EngineBuilder(EWAHBitSet::new).setStorages(
                indexes).createEngine();

        int doc1 = engine.createDocument();
        int doc2 = engine.createDocument();
        int doc3 = engine.createDocument();
        int doc4 = engine.createDocument();

        engine.setAttributeByDocId(doc1, STR_ATTR, ("value1"));
        engine.setAttributeByDocId(doc2, STR_ATTR, ("value1"));
        engine.setAttributeByDocId(doc3, STR_ATTR, ("value2"));
        engine.setAttributeByDocId(doc4, STR_ATTR, ("value2"));
        engine.setAttributeByDocId(doc1, INT_ATTR, 1);
        engine.setAttributeByDocId(doc2, INT_ATTR, 2);
        engine.setAttributeByDocId(doc3, INT_ATTR, 2);
        engine.setAttributeByDocId(doc4, INT_ATTR, 2);
        engine.setAttributeByDocId(doc1, STR_ATTR2, "abc");
        engine.setAttributeByDocId(doc2, STR_ATTR2, "aa");
        engine.setAttributeByDocId(doc3, STR_ATTR2, "b");
        engine.setAttributeByDocId(doc4, STR_ATTR2, "zxcvbnm");
        engine.setAttributeByDocId(doc1, STR_ATTR3, "aa bb cc");
        engine.setAttributeByDocId(doc2, STR_ATTR3, "aa");
        engine.setAttributeByDocId(doc3, STR_ATTR3, "bb");
        engine.setAttributeByDocId(doc4, STR_ATTR3, "cc bb");

        Query r1 = Query.build().query(STR_ATTR, "value1")
                .query(INT_ATTR, StorageRangeBase.generateRequest(2, 2));
        assertEquals(Arrays.asList(doc2), engine.executeQuery(r1));

        Query r2 = Query.build().query(STR_ATTR, "value1")
                .query(INT_ATTR, StorageRangeBase.generateRequest(1, 2));
        assertEquals(Arrays.asList(doc1, doc2), engine.executeQuery(r2));

        Query r3 = Query.build().query(STR_ATTR, "value2")
                .query(INT_ATTR, StorageRangeBase.generateRequest(2, 2));
        assertEquals(Arrays.asList(doc3, doc4), engine.executeQuery(r3));

        Query r4 = Query.build().query(INT_ATTR,
                StorageRangeBase.generateRequest(3, 3));
        assertEquals(Arrays.asList(), engine.executeQuery(r4));

        Query r5 = Query.build()
                .query(INT_ATTR, StorageRangeBase.generateRequest(1, 1))
                .query(STR_ATTR, "value2");
        assertEquals(Arrays.asList(), engine.executeQuery(r5));

        Query r6 = Query.build().query(STR_ATTR2,
                StorageRangeBase.generateRequest("abc", "zxcvbnm"));
        assertEquals(Arrays.asList(doc1, doc3, doc4), engine.executeQuery(r6));

        Query r7 = Query.build().query(STR_ATTR3,
                StorageLucene.generateRequest("aa"));
        assertEquals(Arrays.asList(doc1, doc2), engine.executeQuery(r7));

        Query r8 = Query.build().query(STR_ATTR3,
                StorageLucene.generateRequest("b*"));
        assertEquals(Arrays.asList(doc1, doc3, doc4), engine.executeQuery(r8));
    }
}
