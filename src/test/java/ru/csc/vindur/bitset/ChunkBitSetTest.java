package ru.csc.vindur.bitset;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;

import org.junit.Test;

public class ChunkBitSetTest {

    private static final Supplier<BitArray> supplier = ChunkBitArray::new;

    @Test
    public void emptyBitSetTest() {
        assertEquals(0, supplier.get().cardinality());
        assertEquals(Arrays.asList(), supplier.get().toIntList());
    }

    @Test
    public void randomPointsTest() {
        BitArray bitSet = supplier.get();

        Random random = new Random(8);
        SortedSet<Integer> expected = new TreeSet<>();
        for (int i = 0; i < 100000; i++) {
            int generated = random.nextInt(Integer.MAX_VALUE);
            expected.add(generated);
        }
        bitSet.set(expected);
        assertEquals(expected.size(), bitSet.cardinality());
        assertEquals(expected, new TreeSet<>(bitSet.toIntList()));
    }

    @Test
    public void simpleOperationsTest() {
        BitArray bitSet = supplier.get().set(0).set(7).set(Integer.MAX_VALUE);
        BitArray secondBitSet = supplier.get().set(0).set(5)
                .set(Integer.MAX_VALUE);
        BitArray result;
        result = bitSet.copy().and(secondBitSet);
        assertEquals(Arrays.asList(0, Integer.MAX_VALUE), result.toIntList());
        result = bitSet.copy().or(secondBitSet);
        assertEquals(Arrays.asList(0, 5, 7, Integer.MAX_VALUE),
                result.toIntList());
        result = bitSet.copy().xor(secondBitSet);
        assertEquals(Arrays.asList(5, 7), result.toIntList());
    }
}
