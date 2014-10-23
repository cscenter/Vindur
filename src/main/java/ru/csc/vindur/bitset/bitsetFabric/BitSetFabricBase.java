package ru.csc.vindur.bitset.bitsetFabric;

import java.util.Collection;

import ru.csc.vindur.bitset.BitSet;

public abstract class BitSetFabricBase implements BitSetFabric {

	@Override
	public BitSet newInstance(Collection<Integer> intCollection) {
		return newInstance(intCollection, false);
	}

}
