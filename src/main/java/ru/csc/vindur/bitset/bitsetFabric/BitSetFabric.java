package ru.csc.vindur.bitset.bitsetFabric;

import java.util.Collection;

import ru.csc.vindur.bitset.BitSet;

public interface BitSetFabric {
	BitSet newInstance();

	BitSet newInstance(Collection<Integer> intCollection);

	BitSet newInstance(BitSet resultSet);
}
