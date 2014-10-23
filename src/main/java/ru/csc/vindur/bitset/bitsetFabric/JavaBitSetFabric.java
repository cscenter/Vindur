package ru.csc.vindur.bitset.bitsetFabric;

import java.util.Collection;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.JavaBitSet;

public class JavaBitSetFabric extends BitSetFabricBase {

	@Override
	public BitSet newInstance() {
		return new JavaBitSet();
	}

	@Override
	public BitSet newInstance(Collection<Integer> intCollection,
			boolean isSorted) {
		return new JavaBitSet(intCollection);
	}

}
