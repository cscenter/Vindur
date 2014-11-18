package ru.csc.vindur.storage;

import org.junit.Before;
import org.junit.Test;
import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.EWAHBitSet;
import ru.csc.vindur.document.Value;

import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

/**
 * @author Andrey Kokorev
 *         Created on 19.11.2014.
 */
public class StorageHierarchyTest {
    private Supplier<BitSet> bitSetSupplier;
    private StorageHierarchy storageHierarchy;

    @Before
    public void createStorage()
    {
        bitSetSupplier = EWAHBitSet::new;
        storageHierarchy = new StorageHierarchy(bitSetSupplier);
    }

    @Test
    public void simpleHeirarchyTest() {
        /*
                1
             /  |  \
            2   3   4
            |   | \
            5   6  7
                   |
                   8
        */
        storageHierarchy.add(1, new Value("1"));

        storageHierarchy.add(2, new Value("2", 1));
        storageHierarchy.add(3, new Value("3", 1));
        storageHierarchy.add(4, new Value("4", 1));

        storageHierarchy.add(5, new Value("5", 2));

        storageHierarchy.add(6, new Value("6", 3));
        storageHierarchy.add(7, new Value("7", 3));

        storageHierarchy.add(8, new Value("8", 7));

        BitSet childTree1 = bitSetSupplier.get().set(1).set(2).set(3).set(4).set(5).set(6).set(7).set(8);
        assertEquals(childTree1, storageHierarchy.findChildTree("1"));

        BitSet childTree2 = bitSetSupplier.get().set(2).set(5);
        assertEquals(childTree2, storageHierarchy.findChildTree("2"));

        BitSet childTree3 = bitSetSupplier.get().set(3).set(6).set(7).set(8);
        assertEquals(childTree3, storageHierarchy.findChildTree("3"));

        BitSet childTree4 = bitSetSupplier.get().set(4);
        assertEquals(childTree4, storageHierarchy.findChildTree("4"));
    }


    @Test
    public void manyHierarchiesTest() {
        /*
                1           5          7
             /  |  \        |     /  /   \  \
            2   3   4       6    8  9    10  11
        */

        storageHierarchy.add(1, new Value("1"));

        storageHierarchy.add(2, new Value("2", 1));
        storageHierarchy.add(3, new Value("3", 1));
        storageHierarchy.add(4, new Value("4", 1));

        storageHierarchy.add(5, new Value("5"));
        storageHierarchy.add(6, new Value("6", 5));

        storageHierarchy.add(7,  new Value("7"));
        storageHierarchy.add(8,  new Value("8",  7));
        storageHierarchy.add(9,  new Value("9",  7));
        storageHierarchy.add(10, new Value("10", 7));
        storageHierarchy.add(11, new Value("11", 7));

        BitSet childTree1 = bitSetSupplier.get().set(1).set(2).set(3).set(4);
        assertEquals(childTree1, storageHierarchy.findChildTree("1"));

        BitSet childTree5 = bitSetSupplier.get().set(5).set(6);
        assertEquals(childTree5, storageHierarchy.findChildTree("5"));

        BitSet childTree7 = bitSetSupplier.get().set(7).set(8).set(9).set(10).set(11);
        assertEquals(childTree7, storageHierarchy.findChildTree("7"));
    }

    @Test
    public void emptyResultTest() {
        BitSet childTreeEmpty = bitSetSupplier.get();
        assertEquals(childTreeEmpty, storageHierarchy.findChildTree("1"));
    }
}
