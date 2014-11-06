package ru.csc.vindur.bitset;

import java.util.Collection;
import java.util.List;

//TODO я тут подумал, что еще неплохо бы иметь unmodifibleBitSet - с реализацией всего, кроме set
//И cloneable не нужен, используется только в одном месте

public interface BitSet extends Cloneable {

	public BitSet set(int index);
	
	public BitSet set(Collection<Integer> collection);
	
	/**
	 * List is guaranteed to be sorted by index
	 * @return sorted by index collection of setted bits
	 */
	public List<Integer> toIntList();

	public int cardinality();

	public BitSet and(BitSet other);
	
	public BitSet or(BitSet docsBitSet);

    public BitSet xor(BitSet other);
    
    public BitSet clone();
}
