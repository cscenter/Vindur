package ru.csc.vindur.bitset;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;

public class BitSetUtils {
	public static final BitSet EMPTY_BITSET = new BitSet(1);

    //TODO: плохо. Слишком большой объем списка, вовзращается конкретный класс

    //TODO: используется в одном месте, зачем оно тут?
	//skrivohatskiy 24.10.14 Этот класс был написан до JavaBitSet и использовался во многих местах
	//все методы будут перенесены в JavaBitSet, а этот класс будет удален
	public static ArrayList<Integer> bitSetToArrayList(BitSet bitSet) {
		ArrayList<Integer> result = new ArrayList<>(bitSet.cardinality());
		for (int id = bitSet.nextSetBit(0); id != -1; id = bitSet.nextSetBit(id + 1)) { //todo проще описать через do while
			result.add(id);
		}
		return result;
	}

    //TODO: используется в одном месте, зачем оно тут?
	public static BitSet intCollectionToBitSet(Collection<Integer> collection) {
		BitSet resultBitSet = new BitSet();
		for (int docId: collection) {
			resultBitSet.set(docId);
		}
		return resultBitSet;
	}

	public static BitSet copyOf(BitSet resultSet) {
		return (BitSet) resultSet.clone(); // BitSet is Cloneable
	}
}
