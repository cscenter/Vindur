package ru.csc.vindur.storage;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.ROBitSet;
import ru.csc.vindur.document.Value;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Andrey Kokorev
 *         Created on 19.11.2014.
 */
public class StorageHierarchy implements HierarchyStorage {
    private Supplier<BitSet> bitSetSupplier;
    //These maps are sharing same Nodes
    private Map<String, Node>  valueMap;   //value -> Node { BitSet{childTree ids}
    private Map<Integer, Node> idMap;      //docId -> Node { BitSet{childTree ids}
    private int size = 0;

    public StorageHierarchy(Supplier<BitSet> bitSetSupplier) {
        this.bitSetSupplier = bitSetSupplier;
        this.valueMap = new HashMap<>();
        this.idMap = new HashMap<>();
    }

    @Override
    public ROBitSet findChildTree(String root) {
        Node result = valueMap.get(root);

        if(result != null) return result.getChildTree().asROBitSet();
        return bitSetSupplier.get();
    }

    @Override
    public long size() {
        return size;
    }

    /**
     * Adds value to storage. Values have to be added from root to leaves in hierarchy,
     * otherwise findChildTree behaviour is undefined
     */
    @Override
    public void add(int docId, Value value) {
        Node parent = idMap.get(value.getParentId());
        Node node = new Node(bitSetSupplier.get().set(docId), parent);

        //Mark that this is children of every parent Node
        while(parent != null) {
            parent.getChildTree().set(docId);
            parent = parent.getParent();
        }

        valueMap.put(value.getValue(), node);
        idMap.put(docId, node);

        size++;
    }

    private class Node {
        private final BitSet childTree;
        private Node parent;

        Node(BitSet childTree, @Nullable Node parent) {
            this.childTree = childTree;
            this.parent = parent;
        }

        public BitSet getChildTree() {
            return childTree;
        }

        public Node getParent() {
            return parent;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Node node = (Node) o;

            if (!childTree.equals(node.childTree)) return false;
            if (parent != null ? !parent.equals(node.parent) : node.parent != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = childTree.hashCode();
            result = 31 * result + (parent != null ? parent.hashCode() : 0);
            return result;
        }
    }
}
