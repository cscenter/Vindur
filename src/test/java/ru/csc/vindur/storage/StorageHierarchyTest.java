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
    public void createSupplier()
    {
        bitSetSupplier = EWAHBitSet::new;
    }

    @Test
    public void multiValueChildTreeTest() {
        /*    1
            /  \
           2    3
           |
           4
         */
        storageHierarchy = new StorageHierarchy(bitSetSupplier, "1");
        storageHierarchy.addChild("1", "2");
        storageHierarchy.addChild("1", "3");
        storageHierarchy.addChild("2", "4");

        storageHierarchy.add(1, new Value("1"));
        storageHierarchy.add(2, new Value("1"));
        storageHierarchy.add(3, new Value("1"));

        storageHierarchy.add(4, new Value("3"));
        storageHierarchy.add(5, new Value("3"));
        storageHierarchy.add(6, new Value("3"));

        storageHierarchy.add(7, new Value("4"));
        storageHierarchy.add(8, new Value("4"));

        BitSet childTree1 = bitSetSupplier.get().set(1).set(2).set(3).set(4).set(5).set(6).set(7).set(8);
        assertEquals(childTree1, storageHierarchy.findChildTree("1"));

        BitSet childTree4 = bitSetSupplier.get().set(7).set(8);
        assertEquals(childTree4, storageHierarchy.findChildTree("2"));

        BitSet childTree3 = bitSetSupplier.get().set(4).set(5).set(6);
        assertEquals(childTree3, storageHierarchy.findChildTree("3"));
    }

    @Test
    public void simpleChildTreeTest() {
        /*
                1
             /  |  \
            2   3   4
            |   | \
            5   6  7
                   |
                   8
        */
        storageHierarchy = new StorageHierarchy(bitSetSupplier, "1");
        storageHierarchy.addChild("1", "2");
        storageHierarchy.addChild("1", "3");
        storageHierarchy.addChild("1", "4");

        storageHierarchy.addChild("2", "5");
        storageHierarchy.addChild("3", "6");
        storageHierarchy.addChild("3", "7");

        storageHierarchy.addChild("7", "8");
        
        storageHierarchy.add(1, new Value("1"));

        storageHierarchy.add(2, new Value("2"));
        storageHierarchy.add(3, new Value("3"));
        storageHierarchy.add(4, new Value("4"));

        storageHierarchy.add(5, new Value("5"));

        storageHierarchy.add(6, new Value("6"));
        storageHierarchy.add(7, new Value("7"));

        storageHierarchy.add(8, new Value("8"));

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
    public void emptyChildTreeResultTest() {
        BitSet childTreeEmpty = bitSetSupplier.get();
        storageHierarchy = new StorageHierarchy(bitSetSupplier, "1");
        assertEquals(childTreeEmpty, storageHierarchy.findChildTree("2"));
    }

    @Test
    public void simpleNodeTreeTest() {
        /*
                1
             /  |  \
            2   3   4
            |   | \
            5   6  7
                   |
                   8
        */
        storageHierarchy = new StorageHierarchy(bitSetSupplier, "1");
        storageHierarchy.addChild("1", "2");
        storageHierarchy.addChild("1", "3");
        storageHierarchy.addChild("1", "4");
        storageHierarchy.addChild("2", "5");
        storageHierarchy.addChild("3", "6");
        storageHierarchy.addChild("3", "7");
        storageHierarchy.addChild("7", "8");

        storageHierarchy.add(1, new Value("1"));

        storageHierarchy.add(2, new Value("2"));
        storageHierarchy.add(3, new Value("3"));
        storageHierarchy.add(4, new Value("4"));

        storageHierarchy.add(5, new Value("5"));

        storageHierarchy.add(6, new Value("6"));
        storageHierarchy.add(7, new Value("7"));

        storageHierarchy.add(8, new Value("8"));

        BitSet node1 = bitSetSupplier.get().set(1);
        assertEquals(node1, storageHierarchy.findNode("1"));

        BitSet node2 = bitSetSupplier.get().set(2);
        assertEquals(node2, storageHierarchy.findNode("2"));

        BitSet node3 = bitSetSupplier.get().set(3);
        assertEquals(node3, storageHierarchy.findNode("3"));

        BitSet node8 = bitSetSupplier.get().set(8);
        assertEquals(node8, storageHierarchy.findNode("8"));
    }

    @Test
    public void multiValueNodeTest() {
                /*    1
            /  \
           2    3
           |
           4
         */
        storageHierarchy = new StorageHierarchy(bitSetSupplier, "1");
        storageHierarchy.addChild("1", "3");
        storageHierarchy.addChild("2", "4");
        storageHierarchy.addChild("1", "2");


        storageHierarchy.add(1, new Value("1"));
        storageHierarchy.add(2, new Value("1"));
        storageHierarchy.add(3, new Value("1"));

        storageHierarchy.add(4, new Value("3"));
        storageHierarchy.add(5, new Value("3"));
        storageHierarchy.add(6, new Value("3"));

        storageHierarchy.add(7, new Value("4"));
        storageHierarchy.add(8, new Value("4"));

        BitSet node1 = bitSetSupplier.get().set(1).set(2).set(3);
        assertEquals(node1, storageHierarchy.findNode("1"));

        BitSet node2 = bitSetSupplier.get();
        assertEquals(node2, storageHierarchy.findNode("2"));

        BitSet node3 = bitSetSupplier.get().set(4).set(5).set(6);
        assertEquals(node3, storageHierarchy.findNode("3"));

        BitSet node4 = bitSetSupplier.get().set(7).set(8);
        assertEquals(node4, storageHierarchy.findNode("4"));

    }
}
