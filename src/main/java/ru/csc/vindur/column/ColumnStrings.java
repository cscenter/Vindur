package ru.csc.vindur;

import com.google.common.collect.Sets;

import java.util.*;

/**
 * @author: Phillip Delgyado
 * Date: 30.10.13 17:40
 */
public final class ColumnStrings implements IColumn
{
    private Map<String,TreeSet<Integer>> values; //value->{itemId}
    private int size;
    private int maxsize;

    public ColumnStrings(int maxsize)
    {
        values = new HashMap<>();
        size=0;
        this.maxsize=maxsize;
    }

    @Override
    public long size()
    {
        return size;
    }

    @Override
    public long expectAmount(String value)
    {
        return size/100+1;
    }

    @Override
    public void add(int docId, Value vvalue)
    {
        String value = vvalue.getValue();
        TreeSet<Integer> vals = values.get(value);
        if (vals==null)
        {
            vals = new TreeSet<>();
            values.put(value,vals);
        }
        vals.add(docId);
        size++;
    }

    @Override
    public void remove(int docId, Value oldValue)
    {
        String value = oldValue.getValue();
        TreeSet<Integer> vals = values.get(value);
        if (vals==null) return;
        vals.remove(docId);
        size--;
    }

    @Override
    public Collection<Integer> getAll()
    {
        TreeSet<Integer> rs = Sets.newTreeSet();
        for (TreeSet s : values.values())
         rs.addAll(s);
        Collection<Integer> ids = new ArrayList<>();
        for (Integer itemId : rs )
          ids.add(itemId);
        return ids;
    }

    @Override
    public Collection<Integer> findList(String value)
    {
       Collection<Integer> items = new ArrayList();
       if (!values.containsKey(value))
           return Collections.EMPTY_LIST;

        for (Integer itemId : values.get(value) )
          items.add(itemId);

        return items;
    }

    @Override
    public BitSet findSet(String match)
    {
        BitSet s = new BitSet(maxsize);
        for (int i : findList(match))
         s.set(i);
        return s;
    }



}
