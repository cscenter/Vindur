package ru.csc.vindur.storage;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.ROBitSet;

import java.util.*;
import java.util.function.Supplier;

/**
 * @author Andrey Kokorev
 *         Created on 19.11.2014.
 */
public class StorageHierarchy extends StorageBase<String, String>
{
    public final String ROOT = null;
    private Supplier<BitSet> bitSetSupplier;
    private Map<String, BitSetNode> storage;   //value -> {BitSet of subtree, BitSet of node}
    private Hierarchy hierarchy;

    public StorageHierarchy(Supplier<BitSet> bitSetSupplier)
    {
    	super(String.class, String.class);
        this.bitSetSupplier = bitSetSupplier;
        this.storage = new HashMap<>();
        this.hierarchy = new Hierarchy(ROOT);
        storage.put(ROOT, new BitSetNode(bitSetSupplier.get(), bitSetSupplier.get()));
    }

    public ROBitSet findChildTree(String root)
    {
        BitSetNode result = storage.get(root);
        if(result == null) return bitSetSupplier.get();
        return result.getSubTree().asROBitSet();
    }

    @Override
    public void add(int docId, String value)
    {
        if(!storage.containsKey(value)) return; //TODO: error handling
        //Set docId in node bitset
        storage.get(value).getNode().set(docId);

        //Set docId as member of all parent subtrees
        List<String> pathToRoot = hierarchy.getPathToRoot(value);
        for(String node : pathToRoot)
        {
            storage.get(node).setSubTree(docId);
        }
        incrementDocumentsCount();
    }

    public void addChild(String parent, String node)
    {
        if(node == null)
            throw new IllegalArgumentException("'null' can't be value of 'node' argument");
        hierarchy.addChild(parent, node);
        storage.put(node, new BitSetNode(bitSetSupplier.get(), bitSetSupplier.get()));
    }

    private class Hierarchy
    {
        private Map<String, String> tree;  //node -> parent;
        private String root;

        public Hierarchy(String root)
        {
            this.root = root;
            this.tree = new HashMap<>();
            tree.put(root, null);
        }

        public List<String> getPathToRoot(String node)
        {
            HashSet<String> visited = new HashSet<>();
            List<String> result = new ArrayList<>();

            visited.add(node);
            result.add(node);

            if(node.equals(root)) return result;

            node = tree.get(node);
            while(node != null && !node.equals(root))
            {
                if(visited.contains(node))
                { //cycle found
                    throw new HierarchyCorruptedException();
                }
                result.add(node);
                visited.add(node);
                node = tree.get(node);
            }

            result.add(root);
            return result;
        }

        public void addChild(String parent, String child)
        {
            tree.put(child, parent);
            try
            { //check if root is reachable
                getPathToRoot(child);
            } catch (HierarchyCorruptedException e) { //if not, roll back hierarchy state and pass exception
                tree.remove(child);
                throw e;
            }
        }
    }

    public static class HierarchyCorruptedException extends RuntimeException
    {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public HierarchyCorruptedException()
        {
            super();
        }
    }

    private class BitSetNode
    {
        private BitSet node, subTree;

        BitSetNode(BitSet node, BitSet subTree)
        {
            this.node = node;
            this.subTree = subTree;
        }

        BitSet getNode()
        {
            return node;
        }

        BitSet getSubTree()
        {
            return subTree;
        }

        void setSubTree(Integer id)
        {
            subTree.set(id);
        }
    }

	@Override
	public ROBitSet findSet(String request) {
        BitSetNode result = storage.get(request);
        if(result == null) return bitSetSupplier.get();
        return result.getNode().asROBitSet();
	}

	@Override
	public boolean checkValue(String value, String request) {
		// TODO Auto-generated method stub
		return false;
	}
}
