package ru.csc.vindur.storage;

import javax.annotation.concurrent.ThreadSafe;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.document.Value;

/**
 * @author: Phillip Delgyado
 * Date: 31.10.13 3:50
 */
@ThreadSafe
public interface Storage
{
    public long size(); // TODO: difference between size() and expectedAmount() ???
    public long expectedAmount(String value);
    public void add(int docId, Value value);
    public BitSet findSet(String match);
}
