package ru.csc.vindur.storage;

/**
 * Created by Pavel Chursin on 05.10.2014.
 */

public enum StorageType
{
	/**
	 * Value is String. Request is String
	 */
    STRING,
	/**
	 * Value is Integer. Request is Integer
	 */
    INTEGER,
	/**
	 * Value is Integer. Request is Integer[2](closed segment)
	 */
    RANGE_INTEGER,
	/**
	 * Value is String. Request is String[2](closed segment)
	 */
    RANGE_STRING
}
