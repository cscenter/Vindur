package ru.csc.vindur.test.comparison;

import ru.csc.vindur.bitset.BitArray;
import ru.csc.vindur.bitset.ROBitArray;
import ru.csc.vindur.storage.RangeRequest;
import ru.csc.vindur.storage.StorageRangeBase;

import java.util.*;

/**
 * @author Andrey Kokorev
 *         Created on 13.12.2014.
 */
public class StorageIntLegacy extends StorageRangeBase<Integer> {
    private List<Record> values = new ArrayList<>(); //а может к каждой записи в values писать list itemId?
    private boolean isSorted = false;

    public StorageIntLegacy()
    {
        super(Integer.class);
    }

    @Override
    public final void add(int docId, Integer value)
    {
        Record ir = new Record();
        ir.itemId = docId;
        ir.value = value;
        values.add(ir);
        isSorted = false;
    }

    public static int leftBorder(List<Record> data, Integer value, int idx)
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

    public static int rightBorder(List<Record> data, Integer value, int idx)
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

    public ROBitArray findSet(RangeRequest request)
    {
        if (!isSorted)
        {
            Collections.sort(values, comarator);
            isSorted=true;
        }
        Record low = new Record();
        low.value = (Integer) request.getLowBound();
        Record high = new Record();
        high.value = (Integer) request.getUpperBound();
        int lowIdx = leftBorder(values, low.value, Collections.binarySearch(values, low, comarator));
        int highIdx = rightBorder(values, high.value, Collections.binarySearch(values, high, comarator));
        BitArray result = BitArray.create();
        for(int i = lowIdx + 1; i < highIdx; i++)
        {
            result.set(i);
        }
        return result.asROBitSet();
    }

    public static boolean eq(Record r, Integer value)
    {
        return (r.value.equals(value));
    }

    public int getComplexity() {
        return 100;
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
        Integer value;
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
}
