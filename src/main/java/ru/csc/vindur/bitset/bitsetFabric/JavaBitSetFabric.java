package ru.csc.vindur.bitset.bitsetFabric;

import java.util.Collection;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.JavaBitSet;

public class JavaBitSetFabric implements BitSetFabric {

	@Override
	public BitSet newInstance() {
		return new JavaBitSet();
	}

	@Override
	public BitSet newInstance(Collection<Integer> intCollection) {
		return new JavaBitSet(intCollection);
	}

	@Override
	public BitSet newInstance(BitSet other) {
		return new JavaBitSet((JavaBitSet) other);
	}

}
