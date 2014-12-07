package ru.csc.vindur;

import java.util.HashMap;
import java.util.Map;

import ru.csc.vindur.storage.StorageType;

/**
 * @author: Phillip Delgyado
 */
public class Query {
    private final Map<String, Object> reqs = new HashMap<>(); // aspect->Query

    public static Query build() {
        return new Query();
    }

    /**
     * Add attribute query. Use query type according to specified StorageType
     * 
     * @see StorageType
     */
    public Query query(String attribute, Object storageRequest) {
        reqs.put(attribute, storageRequest);
        return this;
    }

    public Map<String, Object> getQueryParts() {
        return reqs;
    }
}