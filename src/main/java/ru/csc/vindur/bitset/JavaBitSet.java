package ru.csc.vindur.bitset;

import java.util.Collection;
import java.util.List;

public class JavaBitSet extends BitSetBase
{

	private final java.util.BitSet bitSet;

	public JavaBitSet() {
		bitSet = new java.util.BitSet();
	}

	private JavaBitSet(java.util.BitSet bitSet) {
		this.bitSet = bitSet;
	}
	
	public JavaBitSet(JavaBitSet other) {
		this.bitSet = BitSetUtils.copyOf(other.bitSet);
	}
	
	public JavaBitSet(Collection<Integer> intCollection) {
		this.bitSet = BitSetUtils.intCollectionToBitSet(intCollection);
	}

	@Override
	public List<Integer> toIntList() {
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

	@Override
	public int cardinality() {
		return bitSet.cardinality();
	}

	@Override
	public BitSet or(BitSet other, boolean forceCreate) {
		java.util.BitSet otherBitSet = ((JavaBitSet) other).bitSet;
		if(forceCreate) {
			java.util.BitSet newBitSet = BitSetUtils.copyOf(bitSet);
			newBitSet.or(otherBitSet);
			return new JavaBitSet(newBitSet);
		}
		
		bitSet.or(otherBitSet);
		return this;
	}

}
