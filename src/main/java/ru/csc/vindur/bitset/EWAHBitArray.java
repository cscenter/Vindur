package ru.csc.vindur.bitset;

import java.util.Iterator;
import java.util.List;

import com.googlecode.javaewah.EWAHCompressedBitmap;

public class EWAHBitArray implements BitArray {

    private final EWAHCompressedBitmap bitSet;

    public EWAHBitArray() {
        bitSet = new EWAHCompressedBitmap();
    }

    private EWAHBitArray(EWAHCompressedBitmap bitSet) {
        this.bitSet = bitSet;
    }

    @Override
    public BitArray and(ROBitArray other) {
        EWAHCompressedBitmap otherBitSet = ((EWAHBitArray) other).bitSet;
        return new EWAHBitArray(bitSet.and(otherBitSet));
    }

    @Override
    public BitArray set(int index) {
        bitSet.set(index);
        return this;
    }

    @Override
    public List<Integer> toIntList() {
        return bitSet.toList();
    }

    @Override
    public int cardinality() {
        return bitSet.cardinality();
    }

    @Override
    public BitArray or(ROBitArray other) {
        EWAHCompressedBitmap otherBitSet = ((EWAHBitArray) other).bitSet;
        return new EWAHBitArray(bitSet.or(otherBitSet));
    }

    @Override
    public BitArray xor(ROBitArray other) {
        EWAHCompressedBitmap otherBitSet = ((EWAHBitArray) other).bitSet;
        return new EWAHBitArray(bitSet.xor(otherBitSet));
    }

    public BitArray copy() {
        return new EWAHBitArray(this.bitSet.clone());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bitSet == null) ? 0 : bitSet.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        EWAHBitArray other = (EWAHBitArray) obj;
        if (bitSet == null) {
            if (other.bitSet != null) {
                return false;
            }
        } else if (!bitSet.equals(other.bitSet)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return bitSet.toString();
    }

    @Override
    public Iterator<Integer> iterator() {
        return bitSet.iterator();
    }
}
