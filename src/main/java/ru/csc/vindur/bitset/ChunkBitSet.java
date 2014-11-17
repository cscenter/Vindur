package ru.csc.vindur.bitset;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Supplier;

public class ChunkBitSet implements BitSet {
	private final static int CHUNK_SIZE = 1 << 12; // 4k
	private final Supplier<BitSet> supplier;
	private final SortedMap<Integer, BitSet> chunks; // chunk index -> bitset
	private int cardinality;

	public ChunkBitSet() {
		this(EWAHBitSet::new);
	}

	public ChunkBitSet(Supplier<BitSet> supplier) {
		this.supplier = supplier;
		this.chunks = new TreeMap<>();
		this.cardinality = 0;
	}
	
	private ChunkBitSet(ChunkBitSet other) {
		this(other.supplier);
		// TODO find out the better way
		for(Entry<Integer, BitSet> chunkEntry: other.chunks.entrySet()){
			this.chunks.put(chunkEntry.getKey(), chunkEntry.getValue().copy());
		}
		this.cardinality = other.cardinality;
	}

	@Override
	public List<Integer> toIntList() {
		List<Integer> result = new ArrayList<>(cardinality());
		for(Entry<Integer, BitSet> chunkEntry: chunks.entrySet()) {
			int chunkBegin = chunkEntry.getKey() * CHUNK_SIZE;
			List<Integer> chunkResult = chunkEntry.getValue().toIntList();
			// TODO be careful with J8 magic
			chunkResult.stream().map((id) -> id + chunkBegin).forEach(result::add);
		}
		return result;
	}

	@Override
	public int cardinality() {
		return cardinality;
	}

	@Override
	public BitSet copy() {
		return new ChunkBitSet(this);
	}

	@Override
	public BitSet set(int index) {
		int chunkIndex = index / CHUNK_SIZE;
		index = index - chunkIndex * CHUNK_SIZE;
		BitSet chunk = chunks.get(chunkIndex);
		if(chunk == null) {
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
		public BitSet perform(BitSet left, ROBitSet right); 
	}
	
	private BitSet performOperation(ROBitSet oth, Operation operation) {
		ChunkBitSet other = (ChunkBitSet) oth;
		Iterator<Entry<Integer, BitSet>> chunksIterator = chunks.entrySet().iterator();
		while(chunksIterator.hasNext()) {
			Entry<Integer, BitSet> chunkEntry = chunksIterator.next();
			cardinality -= chunkEntry.getValue().cardinality();
			
			BitSet otherChunk = other.chunks.get(chunkEntry.getKey());
			if(otherChunk == null) {
				chunksIterator.remove();
				break;
			}
			BitSet result = operation.perform(chunkEntry.getValue(), otherChunk);
			int newCardinality = result.cardinality();
			chunkEntry.setValue(result);
			cardinality += newCardinality;
			if(newCardinality == 0) {
				chunksIterator.remove();
			}
		}
		return this;
	}
	
	@Override
	public BitSet and(ROBitSet other) {
		return performOperation(other, (left, right) -> left.and(right));
	}

	@Override
	public BitSet or(ROBitSet other) {
		return performOperation(other, (left, right) -> left.or(right));
	}

	@Override
	public BitSet xor(ROBitSet other) {
		return performOperation(other, (left, right) -> left.xor(right));
	}
}
