package ru.csc.vindur.bitset;

import java.util.Iterator;

public class JavaBitArray implements BitArray {

    private final java.util.BitSet bitSet;

    public JavaBitArray() {
        bitSet = new java.util.BitSet();
    }

    private JavaBitArray(java.util.BitSet bitSet) {
        this.bitSet = bitSet;
    }

    @Override
    public BitArray and(ROBitArray other) {
        java.util.BitSet otherBitSet = ((JavaBitArray) other).bitSet;
        bitSet.and(otherBitSet);
        return this;
    }

    @Override
    public BitArray set(int index) {
        bitSet.set(index);
        return this;
    }

    @Override
    public int cardinality() {
        return bitSet.cardinality();
    }

    @Override
    public BitArray or(ROBitArray other) {
        java.util.BitSet otherBitSet = ((JavaBitArray) other).bitSet;
        bitSet.or(otherBitSet);
        return this;
    }

    @Override
    public BitArray xor(ROBitArray other) {
        java.util.BitSet otherBitSet = ((JavaBitArray) other).bitSet;
        bitSet.xor(otherBitSet);
        return this;
    }

    public BitArray copy() {
        return new JavaBitArray((java.util.BitSet) bitSet.clone());
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
        JavaBitArray other = (JavaBitArray) obj;
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
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            private int current = bitSet.nextSetBit(0);

            @Override
            public boolean hasNext() {
                return current != -1;
            }

            @Override
            public Integer next() {
                if (!hasNext()) {
                    throw new IllegalStateException();
                }
                int old = current;
                current = bitSet.nextSetBit(current + 1);
                return old;
            }
        };
    }

}
