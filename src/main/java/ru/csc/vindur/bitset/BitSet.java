package ru.csc.vindur.bitset;

import java.util.Collection;
import java.util.List;

//TODO я тут подумал, что еще неплохо бы иметь unmodifibleBitSet - с реализацией всего, кроме set

public interface BitSet extends ROBitSet {

	public BitSet set(int index);
	
	public BitSet set(Collection<Integer> collection);

    public BitSet and(ROBitSet other);

    public BitSet or(ROBitSet docsBitSet);

    public BitSet xor(ROBitSet other);

    default ROBitSet asROBitSet() {
        return this;
    }
}
