package ru.csc.vindur.storage;

import static org.junit.Assert.assertEquals;

import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.EWAHBitSet;
import ru.csc.vindur.storage.StorageHierarchy.HierarchyCorruptedException;

/**
 * @author Andrey Kokorev Created on 19.11.2014.
 */
public class StorageHierarchyTest {
    private Supplier<BitSet> bitSetSupplier;
    private StorageHierarchy storageHierarchy;

    @Before
    public void createSupplier() {
        bitSetSupplier = EWAHBitSet::new;
        storageHierarchy = new StorageHierarchy(bitSetSupplier);
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

        BitSet childTree1 = bitSetSupplier.get().set(1).set(2).set(3).set(4)
                .set(5).set(6).set(7).set(8);
        assertEquals(childTree1, storageHierarchy.findChildTree("1"));

        BitSet childTree4 = bitSetSupplier.get().set(7).set(8);
        assertEquals(childTree4, storageHierarchy.findChildTree("2"));

        BitSet childTree3 = bitSetSupplier.get().set(4).set(5).set(6);
        assertEquals(childTree3, storageHierarchy.findChildTree("3"));
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

        BitSet childTree1 = bitSetSupplier.get().set(1).set(2).set(3).set(4)
                .set(5).set(6).set(7).set(8);
        assertEquals(childTree1, storageHierarchy.findChildTree("1"));

        BitSet childTree2 = bitSetSupplier.get().set(2).set(5);
        assertEquals(childTree2, storageHierarchy.findChildTree("2"));

        BitSet childTree3 = bitSetSupplier.get().set(3).set(6).set(7).set(8);
        assertEquals(childTree3, storageHierarchy.findChildTree("3"));

        BitSet childTree4 = bitSetSupplier.get().set(4);
        assertEquals(childTree4, storageHierarchy.findChildTree("4"));
    }

    @Test
    public void emptyChildTreeResultTest() {
        BitSet childTreeEmpty = bitSetSupplier.get();
        assertEquals(childTreeEmpty, storageHierarchy.findChildTree("1"));
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

        BitSet node1 = bitSetSupplier.get().set(1);
        assertEquals(node1, storageHierarchy.findSet("1"));

        BitSet node2 = bitSetSupplier.get().set(2);
        assertEquals(node2, storageHierarchy.findSet("2"));

        BitSet node3 = bitSetSupplier.get().set(3);
        assertEquals(node3, storageHierarchy.findSet("3"));

        BitSet node8 = bitSetSupplier.get().set(8);
        assertEquals(node8, storageHierarchy.findSet("8"));
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

        BitSet node1 = bitSetSupplier.get().set(1).set(2).set(3);
        assertEquals(node1, storageHierarchy.findSet("1"));

        BitSet node2 = bitSetSupplier.get();
        assertEquals(node2, storageHierarchy.findSet("2"));

        BitSet node3 = bitSetSupplier.get().set(4).set(5).set(6);
        assertEquals(node3, storageHierarchy.findSet("3"));

        BitSet node4 = bitSetSupplier.get().set(7).set(8);
        assertEquals(node4, storageHierarchy.findSet("4"));

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
