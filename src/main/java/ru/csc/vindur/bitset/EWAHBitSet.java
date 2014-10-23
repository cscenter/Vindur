package ru.csc.vindur.bitset;

import java.util.Collections;
import java.util.List;

import com.googlecode.javaewah.EWAHCompressedBitmap;

public class EWAHBitSet extends BitSetBase {

	private final EWAHCompressedBitmap bitSet;
	
	public EWAHBitSet() {
		bitSet = new EWAHCompressedBitmap();
	}
	
	public EWAHBitSet(EWAHCompressedBitmap bitSet) {
		this.bitSet = bitSet.clone();
	}
	
	public EWAHBitSet(EWAHBitSet other) {
		this.bitSet = other.bitSet.clone();
	}
	
	public EWAHBitSet(List<Integer> intList, boolean isSorted) {
		if(!isSorted) {
			//TODO: find out if this is really necessary
			Collections.sort(intList);
		}
		bitSet = new EWAHCompressedBitmap();
		for(int i: intList) {
			bitSet.set(i);
		}
	}

	@Override
	public BitSet and(BitSet other, boolean forceCreate) {
		EWAHCompressedBitmap otherBitSet = ((EWAHBitSet) other).bitSet;
		return new EWAHBitSet(bitSet.and(otherBitSet));
	}

	@Override
	public BitSet set(int index, boolean forceCreate) {
		return new EWAHBitSet(bitSet.and(EWAHCompressedBitmap.bitmapOf(index)));
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
	public BitSet or(BitSet other, boolean forceCreate) {
		EWAHCompressedBitmap otherBitSet = ((EWAHBitSet) other).bitSet;
		return new EWAHBitSet(bitSet.or(otherBitSet));
	}

}
