package ru.csc.vindur.storage;

/**
 * Created by Pavel Chursin on 05.10.2014.
 */

public enum StorageType {
    /**
     * Value is String. Query is String
     */
    STRING,
    /**
     * Value is Integer. Query is Integer
     */
    INTEGER,
    /**
     * Value is Integer. Query is Integer[2](closed segment)
     */
    RANGE_INTEGER,
    /**
     * Value is String. Query is String[2](closed segment)
     */
    RANGE_STRING,
    /**
     * Value is String. Query is constructed with {@code}
     * StorageLucene::getRequest method
     */
    LUCENE_STRING
}