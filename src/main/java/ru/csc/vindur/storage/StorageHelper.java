package ru.csc.vindur.storage;

import ru.csc.vindur.bitset.BitSet;

import java.util.function.Supplier;

/**
 * @author Andrey Kokorev
 *         Created on 15.10.2014.
 */
public class StorageHelper {

    @SuppressWarnings("rawtypes")
	public static Storage getColumn(StorageType valueType, Supplier<BitSet> bitSetSupplier) {
    	Storage storage;
        switch (valueType) {
            case INTEGER:
                storage = new StorageIntegers(bitSetSupplier);
                break;
            case STRING:
                storage = new StorageStrings(bitSetSupplier);
                break;
            case RANGE_INTEGER:
                storage = new StorageBucketIntegers(bitSetSupplier);
                break;
            default:
            	throw new RuntimeException("Missing case");
        }
        return storage;
    }
}
