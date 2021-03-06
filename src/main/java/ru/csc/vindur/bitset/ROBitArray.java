package ru.csc.vindur.bitset;

import java.util.ArrayList;
import java.util.List;

/**
 * Read-only BitSet
 *
 * @author Andrey Kokorev Created on 15.11.2014.
 */
public interface ROBitArray extends Iterable<Integer> {
    /**
     * List is guaranteed to be sorted by index
     *
     * @return sorted by index collection of setted bits
     */
    default List<Integer> toIntList()
    {
        List<Integer> result = new ArrayList<Integer>(cardinality());
        for (Integer id : this) {
            result.add(id);
        }
        return result;
    }

    public int cardinality();

    public BitArray copy();
}
