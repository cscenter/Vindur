package ru.csc.vindur.bitset.bitsetFabric;

import ru.csc.vindur.bitset.BitSet;

//TODO нужно переделать на Supplier<BitSet> - и отказаться от всех трех классов-фабрик, так оно будет проще

public interface BitSetFabric {
	BitSet newInstance();
}
