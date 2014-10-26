package ru.csc.vindur.column;

import java.util.Collection;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.document.Value;

/**
 * @author: Phillip Delgyado
 * Date: 31.10.13 3:50
 */

//TODO: а давайте переименуем Storage в Storage?

public interface Storage
{
    public long size();
    public long expectAmount(String value);
    public void add(int docId, Value value);
    public Collection<Integer> findList(String match);
    public BitSet findSet(String match);
}
