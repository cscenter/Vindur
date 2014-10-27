package ru.csc.vindur.bitset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JavaBitSet implements BitSet
{

	private final java.util.BitSet bitSet;

	public JavaBitSet() {
		bitSet = new java.util.BitSet();
	}

	public JavaBitSet(JavaBitSet other) {
		this.bitSet = copyOf(other.bitSet);
	}

	public JavaBitSet(Collection<Integer> intCollection) {
		this.bitSet = new java.util.BitSet();
		for (int docId: intCollection) {
			bitSet.set(docId);
		}
	}

	@Override
	public List<Integer> toIntList() {
		ArrayList<Integer> result = new ArrayList<>(bitSet.cardinality());
		int id = 0;
		while((id = bitSet.nextSetBit(id)) != -1) {
			result.add(id);
		}
		return result;
	}

	@Override
	public BitSet and(BitSet other) {
		java.util.BitSet otherBitSet = ((JavaBitSet) other).bitSet;
		bitSet.and(otherBitSet);
		return this;
	}


	@Override
	public BitSet set(int index) {
		bitSet.set(index);
		return this;
	}

	@Override
	public int cardinality() {
		return bitSet.cardinality();
	}

	@Override
	public BitSet or(BitSet other) {
		java.util.BitSet otherBitSet = ((JavaBitSet) other).bitSet;
		bitSet.or(otherBitSet);
		return this;
	}

    @Override
    public BitSet xor(BitSet other) {
        java.util.BitSet otherBitSet = ((JavaBitSet) other).bitSet;
        bitSet.xor(otherBitSet);
        return this;
    }

	private static java.util.BitSet copyOf(java.util.BitSet bitSet) {
		return (java.util.BitSet) bitSet.clone();
	}

}
