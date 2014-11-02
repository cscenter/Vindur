package ru.csc.vindur.bitset.bitsetFabric;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.EWAHBitSet;

public class EWAHBitSetFabric implements BitSetFabric {

	@Override
	public BitSet newInstance() {
		return new EWAHBitSet();
	}

}
