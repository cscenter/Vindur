package ru.csc.vindur.bitset;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Supplier;

public class ChunkBitArray implements BitArray {
    private final static int CHUNK_SIZE = 1 << 12; // 4k
    private final Supplier<BitArray> supplier;
    private final SortedMap<Integer, BitArray> chunks; // chunk index -> bitset
    private int cardinality;

    public ChunkBitArray() {
        this(EWAHBitArray::new);
    }

    public ChunkBitArray(Supplier<BitArray> supplier) {
        this.supplier = supplier;
        this.chunks = new TreeMap<>();
        this.cardinality = 0;
    }

    private ChunkBitArray(ChunkBitArray other) {
        this(other.supplier);
        // TODO find out the better way
        for (Entry<Integer, BitArray> chunkEntry : other.chunks.entrySet()) {
            this.chunks.put(chunkEntry.getKey(), chunkEntry.getValue().copy());
        }
        this.cardinality = other.cardinality;
    }

    @Override
    public List<Integer> toIntList() {
        List<Integer> result = new ArrayList<>(cardinality());
        for (Entry<Integer, BitArray> chunkEntry : chunks.entrySet()) {
            int chunkBegin = chunkEntry.getKey() * CHUNK_SIZE;
            List<Integer> chunkResult = chunkEntry.getValue().toIntList();
            // TODO be careful with J8 magic
            chunkResult.stream().map((id) -> id + chunkBegin)
                    .forEach(result::add);
        }
        return result;
    }

    @Override
    public int cardinality() {
        return cardinality;
    }

    @Override
    public BitArray copy() {
        return new ChunkBitArray(this);
    }

    @Override
    public BitArray set(int index) {
        int chunkIndex = index / CHUNK_SIZE;
        index = index - chunkIndex * CHUNK_SIZE;
        BitArray chunk = chunks.get(chunkIndex);
        if (chunk == null) {
            chunk = supplier.get().set(index);
            cardinality += 1;
            chunks.put(chunkIndex, chunk);
        } else {
            // Maybe add get method to bitSet?
            cardinality -= chunk.cardinality();
            chunk.set(index);
            cardinality += chunk.cardinality();
        }
        return this;
    }

    private static interface Operation {
        public BitArray perform(BitArray left, ROBitArray right);
    }

    private BitArray performOperation(ROBitArray oth, Operation operation) {
        ChunkBitArray other = (ChunkBitArray) oth;
        Iterator<Entry<Integer, BitArray>> chunksIterator = chunks.entrySet()
                .iterator();
        while (chunksIterator.hasNext()) {
            Entry<Integer, BitArray> chunkEntry = chunksIterator.next();
            cardinality -= chunkEntry.getValue().cardinality();

            BitArray otherChunk = other.chunks.get(chunkEntry.getKey());
            if (otherChunk == null) {
                chunksIterator.remove();
                break;
            }
            BitArray result = operation
                    .perform(chunkEntry.getValue(), otherChunk);
            int newCardinality = result.cardinality();
            chunkEntry.setValue(result);
            cardinality += newCardinality;
            if (newCardinality == 0) {
                chunksIterator.remove();
            }
        }
        return this;
    }

    @Override
    public BitArray and(ROBitArray other) {
        return performOperation(other, (left, right) -> left.and(right));
    }

    @Override
    public BitArray or(ROBitArray other) {
        return performOperation(other, (left, right) -> left.or(right));
    }

    @Override
    public BitArray xor(ROBitArray other) {
        return performOperation(other, (left, right) -> left.xor(right));
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {

            private final Iterator<BitArray> chunksIterator = chunks.values()
                    .iterator();
            private Iterator<Integer> currentIterator = getNextIter();

            @Override
            public boolean hasNext() {
                return currentIterator != null && currentIterator.hasNext();
            }

            private Iterator<Integer> getNextIter() {
                if (!chunksIterator.hasNext()) {
                    return null;
                }
                return chunksIterator.next().iterator();
            }

            @Override
            public Integer next() {
                if (!hasNext()) {
                    throw new IllegalStateException();
                }
                Integer id = currentIterator.next();
                if (!currentIterator.hasNext()) {
                    currentIterator = getNextIter();
                }
                return id;
            }
        };
    }
}
