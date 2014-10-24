package ru.csc.vindur.bitset.bitsetFabric;

import java.util.Collection;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.EWAHBitSet;

public class EWAHBitSetFabric implements BitSetFabric {

	@Override
	public BitSet newInstance() {
		return new EWAHBitSet();
	}

	@Override
	public BitSet newInstance(Collection<Integer> intCollection) {
		return new EWAHBitSet(intCollection);
	}

	@Override
	public BitSet newInstance(BitSet other) {
		return new EWAHBitSet((EWAHBitSet) other);
	}

}
