package ru.csc.vindur.storage;

import ru.csc.vindur.bitset.BitSet;

/**
 * @author Andrey Kokorev
 *         Created on 08.11.2014.
 */
public interface RangeStorage extends Storage {
    /**
     *  @return Returns BitSet including documents with attribute values in [low .. high]
     */
    public BitSet findRangeSet(String low, String high);
}
