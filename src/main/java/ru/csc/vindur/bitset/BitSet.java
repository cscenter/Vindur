package ru.csc.vindur.bitset;

import java.util.Collection;

public interface BitSet extends ROBitSet {

    public BitSet set(int index);

    default BitSet set(Collection<Integer> collection) {
        collection.forEach(this::set);
        return this;
    }

    public BitSet and(ROBitSet other);

    public BitSet or(ROBitSet docsBitSet);

    public BitSet xor(ROBitSet other);

    default ROBitSet asROBitSet() {
        return this;
    }
}
