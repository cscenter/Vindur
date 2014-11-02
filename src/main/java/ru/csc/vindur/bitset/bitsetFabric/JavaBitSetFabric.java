package ru.csc.vindur.bitset.bitsetFabric;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.JavaBitSet;

public class JavaBitSetFabric implements BitSetFabric {

	@Override
	public BitSet newInstance() {
		return new JavaBitSet();
	}
}
