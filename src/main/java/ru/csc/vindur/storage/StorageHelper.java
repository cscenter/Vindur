package ru.csc.vindur.storage;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.document.StorageType;

import java.util.function.Supplier;

/**
 * @author Andrey Kokorev
 *         Created on 15.10.2014.
 */
public class StorageHelper {

    public static Storage getColumn(StorageType valueType, Supplier<BitSet> bitSetSupplier) {
        Storage storage;
        switch (valueType) {
            case NUMERIC:
                storage = new StorageBucketIntegers(bitSetSupplier); //todo если есть разные реализации, то лучше их через конфиг подключать или как-нибудь еще
//                storage = new StorageIntegers(bitSetFabric);
                break;
            case ENUM:
                storage = new StorageStrings(bitSetSupplier);
                break;
            case STRING:
            default:
                storage = new StorageStrings(bitSetSupplier);
        }
        return storage;
    }
}
