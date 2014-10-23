package ru.csc.vindur.bitset;

public abstract class BitSetBase implements BitSet {

	@Override
	public BitSet and(BitSet other) {
		return and(other, false);
	}
	
	@Override
	public BitSet set(int index) {
		return set(index, false);
	}

}
