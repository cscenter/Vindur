package ru.csc.vindur.storage;

import javax.annotation.concurrent.ThreadSafe;

import ru.csc.vindur.bitset.ROBitSet;

/**
 * @author: Phillip Delgyado
 * Date: 31.10.13 3:50
 */
@ThreadSafe
public interface ExactStorage extends Storage {
    public ROBitSet findSet(String match);
}
