package ru.csc.vindur.column;

import java.util.*;

import ru.csc.vindur.document.Value;

/**
 * @author: Phillip Delgyado
 * Date: 30.10.13 17:40
 */
public final class ColumnEnums implements IColumn
{
    private Map<String,BitSet> values; //value->set{itemId}
    private int size;
    private int maxsize;

    public ColumnEnums(int maxsize) {
        values = new HashMap<>();
        size = 0;
        this.maxsize = maxsize;
    }

    @Override
    public long size()
    {
        return size;
    }

    @Override
    public long expectAmount(String value) {
        return size / 10000 + 1;
    }

    @Override
    public void add(int docId, Value vvalue) {
        String value = vvalue.getValue();
        BitSet vals = values.get(value);
        if (vals == null) {
            vals = new BitSet(maxsize);
            values.put(value,vals);
        }
        vals.set(docId);
        size++;
    }

    @Override
    public void remove(int docId, Value oldValue) {
        String value = oldValue.getValue();
        BitSet vals = values.get(value);
        if (vals == null) return;
        vals.clear(docId);
        size--;
    }

    @Override
    public Collection<Integer> getAll() {
        BitSet r = new BitSet(maxsize);
        for (BitSet b : values.values())
            r.and(b);
        Collection<Integer> items = new ArrayList<>();
        for (int i = r.nextSetBit(0); i >= 0; i = r.nextSetBit(i+1))
            items.add(i);
        return items;
    }


    @Override
    public Collection<Integer> findList(String value) {
       Collection<Integer> items = new ArrayList<>();
       BitSet r = findSet(value);
       for (int i = r.nextSetBit(0); i >= 0; i = r.nextSetBit(i+1))
            items.add(i);
       return items;
    }

    @Override
    public BitSet findSet(String match) {
       BitSet r = values.get(match);
       if (r == null)
           return new BitSet();

       BitSet l = new BitSet(maxsize);
       l.or(r);

       return l;
    }



}
