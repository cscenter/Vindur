package ru.csc.vindur.bitset;

import java.util.Iterator;

public class JavaBitSet implements BitSet
{

	private final java.util.BitSet bitSet;

	public JavaBitSet() {
		bitSet = new java.util.BitSet();
	}

	private JavaBitSet(java.util.BitSet bitSet) {
		this.bitSet = bitSet;
	}

	@Override
	public BitSet and(ROBitSet other) {
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
	public BitSet or(ROBitSet other) {
		java.util.BitSet otherBitSet = ((JavaBitSet) other).bitSet;
		bitSet.or(otherBitSet);
		return this;
	}

    @Override
    public BitSet xor(ROBitSet other) {
        java.util.BitSet otherBitSet = ((JavaBitSet) other).bitSet;
        bitSet.xor(otherBitSet);
        return this;
    }

    public BitSet copy() {
    	return new JavaBitSet((java.util.BitSet) bitSet.clone());
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bitSet == null) ? 0 : bitSet.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JavaBitSet other = (JavaBitSet) obj;
		if (bitSet == null) {
			if (other.bitSet != null)
				return false;
		} else if (!bitSet.equals(other.bitSet))
			return false;
		return true;
	}

	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {
			private int current = bitSet.nextSetBit(0);
			
			@Override
			public boolean hasNext() {
				return current != -1;
			}

			@Override
			public Integer next() {
				if(!hasNext()) {
					throw new IllegalStateException();
				}
				int old = current;
				current = bitSet.nextSetBit(current + 1);
				return old;
			}
		};
	}

}
