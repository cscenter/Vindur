package ru.csc.vindur.column;

import ru.csc.vindur.bitset.bitsetFabric.BitSetFabric;
import ru.csc.vindur.document.ValueType;

/**
 * @author Andrey Kokorev
 *         Created on 15.10.2014.
 */
public class StorageHelper {

    public static Storage getColumn(ValueType valueType, BitSetFabric bitSetFabric) {
        Storage storage;
        switch (valueType) {
            case NUMERIC:
                storage = new StorageIntegers(bitSetFabric);
                break;
            case ENUM:
                storage = new StorageEnums(bitSetFabric);
                break;
            case STRING:
            default:
                storage = new StorageStrings(bitSetFabric);
        }
        return storage;
    }
}
