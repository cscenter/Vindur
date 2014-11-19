package ru.csc.vindur.storage;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.ROBitSet;
import ru.csc.vindur.document.Value;

import java.util.*;
import java.util.function.Supplier;

/**
 * @author Andrey Kokorev
 *         Created on 19.11.2014.
 */
public class StorageHierarchy implements HierarchyStorage {
    private Supplier<BitSet> bitSetSupplier;
    private Map<String, BitSetNode> storage;   //value -> {BitSet of subtree, BitSet of node}
    private Hierarchy hierarchy;
    private int size = 0;

    public StorageHierarchy(Supplier<BitSet> bitSetSupplier, Hierarchy hierarchy) {
        this.bitSetSupplier = bitSetSupplier;
        this.storage = new HashMap<>();
        this.hierarchy = hierarchy;
        initHierarchy();
    }

    private void initHierarchy() {
        for(String node : hierarchy.getNodeSet())
        {
            storage.put(node, new BitSetNode(bitSetSupplier.get(), bitSetSupplier.get()));
        }
    }

    @Override
    public ROBitSet findChildTree(String root) {
        BitSetNode result = storage.get(root);
        if(result == null) return bitSetSupplier.get();
        return result.getSubTree().asROBitSet();
    }

    @Override
    public ROBitSet findNode(String node) {
        BitSetNode result = storage.get(node);
        if(result == null) return bitSetSupplier.get();
        return result.getNode().asROBitSet();
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public void add(int docId, Value value) {
        String val = value.getValue();
        if(!storage.containsKey(val)) return; //TODO: error handling
        //Set docId in node bitset
        storage.get(val).getNode().set(docId);

        //Set docId as member of all parent subtrees
        List<String> pathToRoot = hierarchy.getPathToRoot(val);
        for(String node : pathToRoot)
        {

            storage.get(node).setSubTree(docId);
        }
        size++;
    }

    public static class Hierarchy {
        private Map<String, String> tree;  //node -> parent;
        private String root;

        public Hierarchy(String root) {
            this.root = root;
            this.tree = new HashMap<>();
            tree.put(root, null);
        }

        public List<String> getPathToRoot(String node) {
            List<String> result = new ArrayList<>();
            result.add(node);
            if(node.equals(root)) return result;

            node = tree.get(node);
            while(node != null && !node.equals(root))
            {
                result.add(node);
                node = tree.get(node);
            }

            result.add(root);
            return result;
        }

        public String getRoot(String root) {
            return root;
        }

        public void addChild(String parent, String child) {
            tree.put(child, parent);
        }

        public Set<String> getNodeSet() {
            return tree.keySet();
        }
    }

    private class BitSetNode {
        private BitSet node, subTree;

        BitSetNode(BitSet node, BitSet subTree) {
            this.node = node;
            this.subTree = subTree;
        }

        BitSet getNode() {
            return node;
        }

        BitSet getSubTree() {
            return subTree;
        }

        void setSubTree(Integer id) {
            subTree.set(id);
        }
    }
}
