package ru.csc.vindur.storage;

import static org.junit.Assert.assertEquals;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.junit.Before;
import org.junit.Test;

import ru.csc.vindur.bitset.BitArray;
import ru.csc.vindur.bitset.EWAHBitArray;

public class StorageLuceneTest {
    private StorageLucene storageLucene;

    @Before
    public void createStorage() {
        storageLucene = new StorageLucene();
    }

    @Test
    public void simpleTest() throws ParseException {
        storageLucene.add(0, "value0");
        storageLucene.add(1, "value1");
        storageLucene.add(2, "value0 value1");

        BitArray expected = new EWAHBitArray().set(0).set(2);
        assertEquals(expected, storageLucene.findSet("value0"));
        expected = new EWAHBitArray().set(1).set(2);
        assertEquals(expected, storageLucene.findSet("value1"));
    }

    @Test
    public void wildcardSearchTest() throws ParseException {
        storageLucene.add(0, "value0");

        BitArray expected = new EWAHBitArray().set(0);
        assertEquals(expected, storageLucene.findSet("v?lu*"));
    }

    @Test
    public void regExpSearchTest() throws ParseException {
        storageLucene.add(0, "value0");

        BitArray expected = new EWAHBitArray().set(0);
        assertEquals(expected, storageLucene.findSet("/[fvs]alue./"));
    }

    @Test
    public void checkValueTest() throws ParseException {
        storageLucene.add(0, "aa bb cc dd");
        assertEquals(
                false,
                storageLucene.checkValue(0, "aa bb cc dd", "ff"));
        assertEquals(
                false,
                storageLucene.checkValue(0, "aa bb cc dd", "ad"));
        assertEquals(
                true,
                storageLucene.checkValue(0, "aa bb cc dd", "bb"));
        assertEquals(
                true,
                storageLucene.checkValue(0, "aa bb cc dd", "cc"));
        assertEquals(
                false,
                storageLucene.checkValue(0, "aa bb cc dd", "aa dd"));
        assertEquals(
                false,
                storageLucene.checkValue(0, "aa bb cc dd", "d*"));
    }
}
