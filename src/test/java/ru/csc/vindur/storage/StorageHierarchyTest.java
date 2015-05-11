package ru.csc.vindur.storage;

import static org.junit.Assert.assertEquals;

import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;

import ru.csc.vindur.bitset.BitArray;
import ru.csc.vindur.bitset.EWAHBitArray;
import ru.csc.vindur.storage.StorageHierarchy.HierarchyCorruptedException;

/**
 * @author Andrey Kokorev Created on 19.11.2014.
 */
public class StorageHierarchyTest {
    private Supplier<BitArray> bitSetSupplier;
    private StorageHierarchy storageHierarchy;

    @Before
    public void createSupplier() {
        bitSetSupplier = EWAHBitArray::new;
        storageHierarchy = new StorageHierarchy();
    }

    @Test
    public void multiValueChildTreeTest() {
        /*
         * ROOT | 1 / \ 2 3 | 4
         */
        storageHierarchy.addChild(storageHierarchy.ROOT, "1");
        storageHierarchy.addChild("1", "2");
        storageHierarchy.addChild("1", "3");
        storageHierarchy.addChild("2", "4");

        storageHierarchy.add(1, ("1"));
        storageHierarchy.add(2, ("1"));
        storageHierarchy.add(3, ("1"));

        storageHierarchy.add(4, ("3"));
        storageHierarchy.add(5, ("3"));
        storageHierarchy.add(6, ("3"));

        storageHierarchy.add(7, ("4"));
        storageHierarchy.add(8, ("4"));

        BitArray childTree1 = bitSetSupplier.get().set(1).set(2).set(3).set(4)
                .set(5).set(6).set(7).set(8);
        assertEquals(childTree1, storageHierarchy.findSet("1"));

        BitArray childTree4 = bitSetSupplier.get().set(7).set(8);
        assertEquals(childTree4, storageHierarchy.findSet("2"));

        BitArray childTree3 = bitSetSupplier.get().set(4).set(5).set(6);
        assertEquals(childTree3, storageHierarchy.findSet("3"));
    }

    @Test
    public void simpleChildTreeTest() {
        /*
         * ROOT | 1 / | \ 2 3 4 | | \ 5 6 7 | 8
         */
        storageHierarchy.addChild(storageHierarchy.ROOT, "1");
        storageHierarchy.addChild("1", "2");
        storageHierarchy.addChild("1", "3");
        storageHierarchy.addChild("1", "4");

        storageHierarchy.addChild("2", "5");
        storageHierarchy.addChild("3", "6");
        storageHierarchy.addChild("3", "7");

        storageHierarchy.addChild("7", "8");

        storageHierarchy.add(1, ("1"));

        storageHierarchy.add(2, ("2"));
        storageHierarchy.add(3, ("3"));
        storageHierarchy.add(4, ("4"));

        storageHierarchy.add(5, ("5"));

        storageHierarchy.add(6, ("6"));
        storageHierarchy.add(7, ("7"));

        storageHierarchy.add(8, ("8"));

        BitArray childTree1 = bitSetSupplier.get().set(1).set(2).set(3).set(4)
                .set(5).set(6).set(7).set(8);
        assertEquals(childTree1, storageHierarchy.findSet("1"));

        BitArray childTree2 = bitSetSupplier.get().set(2).set(5);
        assertEquals(childTree2, storageHierarchy.findSet("2"));

        BitArray childTree3 = bitSetSupplier.get().set(3).set(6).set(7).set(8);
        assertEquals(childTree3, storageHierarchy.findSet("3"));

        BitArray childTree4 = bitSetSupplier.get().set(4);
        assertEquals(childTree4, storageHierarchy.findSet("4"));
    }

    @Test
    public void emptyChildTreeResultTest() {
        BitArray childTreeEmpty = bitSetSupplier.get();
        assertEquals(childTreeEmpty, storageHierarchy.findSet("1"));
    }

    @Test
    public void simpleNodeTreeTest() {
        /*
         * ROOT | 1 / | \ 2 3 4 | | \ 5 6 7 | 8
         */
        storageHierarchy.addChild(storageHierarchy.ROOT, "1");
        storageHierarchy.addChild("1", "2");
        storageHierarchy.addChild("1", "3");
        storageHierarchy.addChild("1", "4");
        storageHierarchy.addChild("2", "5");
        storageHierarchy.addChild("3", "6");
        storageHierarchy.addChild("3", "7");
        storageHierarchy.addChild("7", "8");

        storageHierarchy.add(1, ("1"));

        storageHierarchy.add(2, ("2"));
        storageHierarchy.add(3, ("3"));
        storageHierarchy.add(4, ("4"));

        storageHierarchy.add(5, ("5"));

        storageHierarchy.add(6, ("6"));
        storageHierarchy.add(7, ("7"));

        storageHierarchy.add(8, ("8"));

        BitArray node1 = bitSetSupplier.get().set(1);
        assertEquals(node1, storageHierarchy.findExactSet("1"));

        BitArray node2 = bitSetSupplier.get().set(2);
        assertEquals(node2, storageHierarchy.findExactSet("2"));

        BitArray node3 = bitSetSupplier.get().set(3);
        assertEquals(node3, storageHierarchy.findExactSet("3"));

        BitArray node8 = bitSetSupplier.get().set(8);
        assertEquals(node8, storageHierarchy.findExactSet("8"));
    }

    @Test
    public void multiValueNodeTest() {
        /*
         * ROOT | 1 / \ 2 3 | 4
         */
        storageHierarchy.addChild(storageHierarchy.ROOT, "1");
        storageHierarchy.addChild("1", "3");
        storageHierarchy.addChild("2", "4");
        storageHierarchy.addChild("1", "2");

        storageHierarchy.add(1, ("1"));
        storageHierarchy.add(2, ("1"));
        storageHierarchy.add(3, ("1"));

        storageHierarchy.add(4, ("3"));
        storageHierarchy.add(5, ("3"));
        storageHierarchy.add(6, ("3"));

        storageHierarchy.add(7, ("4"));
        storageHierarchy.add(8, ("4"));

        BitArray node1 = bitSetSupplier.get().set(1).set(2).set(3);
        assertEquals(node1, storageHierarchy.findExactSet("1"));

        BitArray node2 = bitSetSupplier.get();
        assertEquals(node2, storageHierarchy.findExactSet("2"));

        BitArray node3 = bitSetSupplier.get().set(4).set(5).set(6);
        assertEquals(node3, storageHierarchy.findExactSet("3"));

        BitArray node4 = bitSetSupplier.get().set(7).set(8);
        assertEquals(node4, storageHierarchy.findExactSet("4"));

    }

    @Test(expected = HierarchyCorruptedException.class)
    public void hierarchyConsistencyTest() {
        /*
         * 1 / \ 2 3 \ / 4
         */
        storageHierarchy.addChild(storageHierarchy.ROOT, "1");
        storageHierarchy.addChild("1", "3");
        storageHierarchy.addChild("3", "4");
        storageHierarchy.addChild("4", "2");
        storageHierarchy.addChild("2", "1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void hierarchyRootUnmodifiableTest() {
        storageHierarchy.addChild("1", storageHierarchy.ROOT);
    }
}
