package ru.csc.vindur.bitset;

import java.util.Collection;

public interface BitSet {
	/**
	 * Same as <code>and(other, false)</code>
	 * @param other other BitSet. Should have the same type
	 * @return BitSet containing the result of and operation
	 */
	public BitSet and(BitSet other);
	
	/**
	 * @param other other BitSet. Should have the same type
	 * @param forceCreate if true the method create a new BitSet to store result
	 * @return BitSet containing the result of the and operation
	 */
	public BitSet and(BitSet other, boolean forceCreate);

	/**
	 * Same as <code>set(index, false)</code>
	 * @param index
	 * @return BitSet containing the result of the and operation
	 */
	public BitSet set(int index);
	
	/**
	 * @param index
	 * @param forceCreate if true the method create a new BitSet to store result
	 * @return BitSet containing the result of the and operation
	 */
	public BitSet set(int index, boolean forceCreate);
	
	/**
	 * Collection is guaranteed to be sorted
	 * @return sorted collection of setted bits
	 */
	public Collection<Integer> toIntCollection();
}
