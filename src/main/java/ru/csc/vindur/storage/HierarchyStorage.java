package ru.csc.vindur.storage;

import ru.csc.vindur.bitset.ROBitSet;

/**
 * @author Andrey Kokorev
 *         Created on 19.11.2014.
 */
public interface HierarchyStorage extends Storage {
    /**
     * @param root of hierarchy tree
     * @return Returns ROBitSet of every document with attribute values in hierarchy tree
     */
    public ROBitSet findChildTree(String root);
}
