package ru.csc.vindur.storage;

import ru.csc.vindur.bitset.ROBitSet;

/**
 * @author Andrey Kokorev
 *         Created on 19.11.2014.
 */
public interface HierarchyStorage extends Storage {
    /**
     * @param root of hierarchy child ree
     * @return Returns ROBitSet of every document with attribute values in hierarchy child tree
     */
    public ROBitSet findChildTree(String root);

    /**
     *
     * @param node of hierarchy tree
     * @return Returns ROBitSet of every document with attribute value in specified hierarchy position
     */
    public ROBitSet findNode(String node);


    public void addChild(String parent, String node);
}
