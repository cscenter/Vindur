package ru.csc.vindur.bitset;

import java.util.List;

public interface BitSet {

	public BitSet and(BitSet other);

	public BitSet set(int index);
	
	/**
	 * List is guaranteed to be sorted
	 * @return sorted collection of setted bits
	 */
    //TODO если sorted - то стоит указать, по какому параметру )
	public List<Integer> toIntList();

	public int cardinality();

	public BitSet or(BitSet docsBitSet);

    public BitSet xor(BitSet other);
}
