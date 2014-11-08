package ru.csc.vindur.storage;

import ru.csc.vindur.bitset.bitsetFabric.BitSetFabric;
import ru.csc.vindur.document.StorageType;

/**
 * @author Andrey Kokorev
 *         Created on 15.10.2014.
 */
public class StorageHelper {

    public static Storage getColumn(StorageType valueType, BitSetFabric bitSetFabric) {
        Storage storage;
        switch (valueType) {
            case NUMERIC:
                storage = new StorageBucketIntegers(bitSetFabric); //todo если есть разные реализации, то лучше их через конфиг подключать или как-нибудь еще
//                storage = new StorageIntegers(bitSetFabric);
                break;
            case ENUM:
                storage = new StorageStrings(bitSetFabric);
                break;
            case STRING:
            default:
                storage = new StorageStrings(bitSetFabric);
        }
        return storage;
    }
}
