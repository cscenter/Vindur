package ru.csc.vindur.bitset;

import java.util.List;

/**
 * Read-only BitSet
 * @author Andrey Kokorev
 *         Created on 15.11.2014.
 */
public interface ROBitSet {
    /**
     * List is guaranteed to be sorted by index
     * @return sorted by index collection of setted bits
     */
    public List<Integer> toIntList();

    public int cardinality();

    public BitSet copy();
}
