package ru.csc.vindur.bitset;

import java.util.Collection;

public interface BitArray extends ROBitArray {

    public BitArray set(int index);

    default BitArray set(Collection<Integer> collection) {
        collection.forEach(this::set);
        return this;
    }

    public BitArray and(ROBitArray other);

    public BitArray or(ROBitArray docsBitSet);

    public BitArray xor(ROBitArray other);

    default ROBitArray asROBitSet() {
        return this;
    }

    static BitArray create()
    {
        return new EWAHBitArray();
    }
}
