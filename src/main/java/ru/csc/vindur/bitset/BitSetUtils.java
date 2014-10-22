package ru.csc.vindur.bitset;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;

public class BitSetUtils {
	public static final BitSet EMPTY_BITSET = new BitSet(1);

	public static ArrayList<Integer> bitSetToArrayList(BitSet bitSet) {
		ArrayList<Integer> result = new ArrayList<>(bitSet.cardinality());
		for (int id = bitSet.nextSetBit(0); id != 0; id = bitSet.nextSetBit(id + 1)) {
			result.add(id);
		}
		return result;
	}

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
