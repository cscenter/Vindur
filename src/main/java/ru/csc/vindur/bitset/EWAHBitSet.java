package ru.csc.vindur.bitset;

import com.googlecode.javaewah.EWAHCompressedBitmap;

import java.util.Collection;
import java.util.List;

public class EWAHBitSet implements BitSet {

	private final EWAHCompressedBitmap bitSet;
	
	public EWAHBitSet() {
		bitSet = new EWAHCompressedBitmap();
	}

    private EWAHBitSet(EWAHCompressedBitmap bitSet) {
		this.bitSet = bitSet;
	}
	
	public BitSet set(Collection<Integer> intList) {
		for(int i: intList) {
			bitSet.set(i);
		}
		return this;
	}

	@Override
	public BitSet and(ROBitSet other) {
		EWAHCompressedBitmap otherBitSet = ((EWAHBitSet) other).bitSet;
		return new EWAHBitSet(bitSet.and(otherBitSet));
	}

	@Override
	public BitSet set(int index) {
		bitSet.set(index);
		return this;
	}

	@Override
	public List<Integer> toIntList() {
		return bitSet.toList();
	}

	@Override
	public int cardinality() {
		return bitSet.cardinality();
	}

	@Override
	public BitSet or(ROBitSet other) {
		EWAHCompressedBitmap otherBitSet = ((EWAHBitSet) other).bitSet;
		return new EWAHBitSet(bitSet.or(otherBitSet));
	}

    @Override
    public BitSet xor(ROBitSet other) {
        EWAHCompressedBitmap otherBitSet = ((EWAHBitSet) other).bitSet;
        return new EWAHBitSet(bitSet.xor(otherBitSet));
    }
    
    public BitSet copy() {
    	return new EWAHBitSet(this.bitSet.clone());
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
		EWAHBitSet other = (EWAHBitSet) obj;
		if (bitSet == null) {
			if (other.bitSet != null)
				return false;
		} else if (!bitSet.equals(other.bitSet))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return bitSet.toString();
	}
}
