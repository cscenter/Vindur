package ru.csc.vindur.column;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.bitsetFabric.BitSetFabric;
import ru.csc.vindur.document.Value;

import java.util.*;


/**
 * @author Andrey Kokorev
 *         Created on 19.10.2014.
 * For each attribute value stored a BitSet with every docId
 * with lower or equal attribute value
 */
public final class StorageIntegers implements Storage {
    private TreeMap<Integer, BitSet> storage; //key -> bitset of all smaller
    private BitSetFabric bitSetFabric;

    public StorageIntegers(BitSetFabric bitSetFabric) {
        this.storage = new TreeMap<>();
        this.bitSetFabric = bitSetFabric;
    }

    @Override
    public long size() {
        return storage.size();
    }

    @Override
    public long expectAmount(String value) {
        return storage.size() / 1000;
    }

    @Override
    public void add(int docId, Value value) {
        Integer newKey = Integer.parseInt(value.getValue());

        //Set bit in each bitset for greater attribute
        for(Integer key : storage.tailMap(newKey).keySet()) { //тогда уж и менять в tailMap.entry, дешевле будет
            storage.get(key).set(docId);
        }

        if(storage.containsKey(newKey)) { //а что, если значение уже есть, то
            return;
        }
        //otherwise we should add new record to storage
        BitSet bitSet = bitSetFabric.newInstance();
        bitSet.set(docId);

        storage.put(newKey, bitSet);
    }

    @Override
    public Collection<Integer> findList(String strictMatch) {
        return findSet(strictMatch).toIntList();
    }

    @Override
    public BitSet findSet(String strictMatch) {
        Integer key = Integer.parseInt(strictMatch);

        if(!storage.containsKey(key)) return bitSetFabric.newInstance();

        if(storage.firstKey().equals(key)) return storage.get(key);
        BitSet low = storage.get(storage.lowerKey(key));
        return storage.get(key).or(low);  // everything including this or lower, except lower
    }

    class Record implements Comparable<Record>{  //todo а зачем это?
        private Integer key;
        private BitSet value;

        public Record(Integer key, BitSet value) {
            this.key = key;
            this.value = value;
        }

        public void setValue(BitSet value) {
            this.value = value;
        }

        public Integer getKey() {
            return key;
        }

        public BitSet getValue() {
            return value;
        }

        @Override
        public int compareTo(Record o) {
            return Integer.compare(key, o.getKey());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Record record = (Record) o;

            if (!key.equals(record.key)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }
    }
}

