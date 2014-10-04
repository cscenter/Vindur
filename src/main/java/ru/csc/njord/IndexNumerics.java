package ru.csc.njord;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import ru.csc.njord.entity.Value;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author: Phillip Delgyado
 * Date: 30.10.13 17:40
 */
public final class IndexNumerics implements IIndex
{
    private List<Record> values; //а может к каждой записи в values писать list itemId?
    private boolean isSorted=false;

    private NumberConverter converter;

    private int size;
    private int maxsize;

    public IndexNumerics(int maxsize)
    {
        this.values = new ArrayList<>();
        this.size=0;
        this.maxsize=maxsize;
    }

    public IndexNumerics setConverter(NumberConverter converter)
    {
        this.converter = converter;
        return this;
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
    public final void add(int docId, Value value)
    {
        Record ir = new Record();
         ir.itemId= docId;
         ir.value=converter.convertToDecimal(value);
        values.add(ir);
        isSorted=false;
        size++;
    }

    @Override
    public final void remove(int docId, Value oldValue)
    {
        Record ir = new Record();
         ir.itemId= docId;
         ir.value=converter.convertToDecimal(oldValue);
        values.remove(ir);     //todo грязный хак
        isSorted=false;
        size--;
    }

    @Override
    public Collection<Integer> getAll()
    {
        return Lists.transform(values, new Function<Record, Integer>() {
            @Nullable
            @Override
            public Integer apply(@Nullable Record input)
            {
                return input.itemId;
            }
        }) ;
    }

    @Override
    public Collection<Integer> findList(String svalue)
    {
       BigDecimal value = new BigDecimal(svalue);

       if (isSorted==false)
       {
           Collections.sort(values,comarator);
           isSorted=true;
       }
       Collection<Integer> items = new ArrayList<>();

       //найдем какой-то подходящий
       Record key = new Record();
         key.value=value;
        int idx = Collections.binarySearch(values,key,comarator);

        for (int ii=leftBorder(values,value,idx);ii<rightBorder(values,value,idx);ii++)
        {
            items.add(values.get(ii).itemId);
        }

       return items ;
    }




    public static int leftBorder(List<Record> data, BigDecimal value, int idx)
    {
        int ii=idx;
        if (idx<0) return -1;

        while (ii>0)
        {
            Record rt = data.get(ii);
            if (! eq(rt,value)) break;
            ii--;
        }
        return ii;
    }

    public static int rightBorder(List<Record> data, BigDecimal value, int idx)
      {
          int ii=idx+1;
          if (idx<0) return -1;

          while (ii<data.size())
          {
              Record rt = data.get(ii);
              if (! eq(rt,value)) break;
              ii++;
          }
          return ii;
      }

    public static boolean eq(Record r, BigDecimal value)
    {
        return (r.value.equals(value));
    }



    @Override
    public BitSet findSet(String match)
    {
        BitSet s = new BitSet(maxsize);
        for (int i : findList(match))
         s.set(i);
        return s;
    }

    private final Comparator<Record> comarator =
     new Comparator<Record>()
     {
       public int compare(Record o1, Record o2)
       {
           int r;
           r = o1.value.compareTo(o2.value);
           return r;
       }
     };


    private static final class Record
    {
       BigDecimal value;
       int itemId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Record record = (Record) o;

            if (itemId != record.itemId) return false;
            if (value != null ? !value.equals(record.value) : record.value != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = value != null ? value.hashCode() : 0;
            result = 31 * result + (int) (itemId ^ (itemId >>> 32));
            return result;
        }
    }


    public static interface NumberConverter
    {
        public BigDecimal convertToDecimal(Value value);
    }


}
