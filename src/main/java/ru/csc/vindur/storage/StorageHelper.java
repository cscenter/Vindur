package ru.csc.vindur.storage;

import java.util.function.Supplier;

import ru.csc.vindur.bitset.BitSet;

/**
 * @author Andrey Kokorev Created on 15.10.2014.
 */
public class StorageHelper {

    @SuppressWarnings("rawtypes")
    public static StorageBase getColumn(StorageType valueType,
            Supplier<BitSet> bitSetSupplier) {
        StorageBase storage;
        switch (valueType) {
        case INTEGER:
            storage = new StorageExact<Integer>(bitSetSupplier, Integer.class);
            break;
        case STRING:
            storage = new StorageExact<String>(bitSetSupplier, String.class);
            break;
        case RANGE_INTEGER:
            storage = new StorageBucketIntegers(bitSetSupplier);
            break;
        case RANGE_STRING:
            storage = new StorageRange<String>(bitSetSupplier, String.class);
            break;
        case LUCENE_STRING:
            storage = new StorageLucene(bitSetSupplier);
            break;
        default:
            throw new RuntimeException("Missing case");
        }
        return storage;
    }
}
