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
	public BitSet and(BitSet other) {
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
	public BitSet or(BitSet other) {
		EWAHCompressedBitmap otherBitSet = ((EWAHBitSet) other).bitSet;
		return new EWAHBitSet(bitSet.or(otherBitSet));
	}

    @Override
    public BitSet xor(BitSet other) {
        EWAHCompressedBitmap otherBitSet = ((EWAHBitSet) other).bitSet;
        return new EWAHBitSet(bitSet.xor(otherBitSet));
    }
    
    public BitSet clone() {
    	return new EWAHBitSet(this.bitSet.clone());
    }
}
