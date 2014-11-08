package ru.csc.vindur.optimizer;

import ru.csc.vindur.Request;
import ru.csc.vindur.bitset.BitSet;

/**
 * Created by Edgar on 03.11.14.
 */
public interface Step {
    BitSet perform(BitSet bitSet);
}
