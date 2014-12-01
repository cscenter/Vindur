package ru.csc.vindur.optimizer;

import ru.csc.vindur.bitset.ROBitSet;

/**
 * Created by Edgar on 03.11.14.
 */
public interface Step {
    public ROBitSet execute();
}
