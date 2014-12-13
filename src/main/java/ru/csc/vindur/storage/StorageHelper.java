package ru.csc.vindur.storage;


/**
 * @author Andrey Kokorev Created on 15.10.2014.
 */
public class StorageHelper {

    @SuppressWarnings("rawtypes")
    public static Storage getColumn(StorageType valueType) {
        Storage storage;
        switch (valueType) {
        case INTEGER:
            storage = new StorageExact<>(Integer.class);
            break;
        case STRING:
            storage = new StorageExact<>(String.class);
            break;
        case RANGE_INTEGER:
            storage = new StorageBucketIntegers();
            break;
        case RANGE_STRING:
            storage = new StorageRange<>( String.class);
            break;
        case LUCENE_STRING:
            storage = new StorageLucene();
            break;
        default:
            throw new RuntimeException("Missing case");
        }
        return storage;
    }
}
