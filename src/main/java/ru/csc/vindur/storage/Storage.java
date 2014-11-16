package ru.csc.vindur.storage;

import javax.annotation.concurrent.ThreadSafe;

import ru.csc.vindur.bitset.ROBitSet;
import ru.csc.vindur.document.Value;

/**
 * @author: Phillip Delgyado
 * Date: 31.10.13 3:50
 */
@ThreadSafe
public interface Storage
{
    public long size();
    public void add(int docId, Value value);
    public ROBitSet findSet(String match) throws Exception;
}
