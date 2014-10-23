package ru.csc.vindur.bitset;

import java.util.Collection;

public class JavaBitSet extends BitSetBase {

	private final java.util.BitSet bitSet;

	public JavaBitSet() {
		bitSet = new java.util.BitSet();
	}

	private JavaBitSet(java.util.BitSet bitSet) {
		this.bitSet = bitSet;
	}
	
	public JavaBitSet(Collection<Integer> intCollection) {
		this.bitSet = BitSetUtils.intCollectionToBitSet(intCollection);
	}

	@Override
	public Collection<Integer> toIntCollection() {
		return BitSetUtils.bitSetToArrayList(bitSet);
	}

	@Override
	public BitSet and(BitSet other, boolean forceCreate) {
		java.util.BitSet otherBitSet = ((JavaBitSet) other).bitSet;
		if(forceCreate) {
			java.util.BitSet newBitSet = BitSetUtils.copyOf(bitSet);
			newBitSet.and(otherBitSet);
			return new JavaBitSet(newBitSet);
		}
		
		bitSet.and(otherBitSet);
		return this;
	}


	@Override
	public BitSet set(int index, boolean forceCreate) {
		if(forceCreate) {
			java.util.BitSet newBitSet = BitSetUtils.copyOf(bitSet);
			newBitSet.set(index);
			return new JavaBitSet(newBitSet);
		}
		
		bitSet.set(index);
		return this;
	}

}
