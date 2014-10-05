package ru.csc.njord;

import ru.csc.njord.entity.Value;

import java.util.BitSet;
import java.util.Collection;

/**
 * @author: Phillip Delgyado
 * Date: 31.10.13 3:50
 */
public interface IIndex
{
    public long size();
    public long expectAmount(String value);
    public void add(int docId, Value value);
    public void remove(int docId, Value oldValue);
    public Collection<Integer> getAll();
    public Collection<Integer> findList(String match);
    public BitSet findSet(String match);
}
