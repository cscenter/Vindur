package ru.csc.vindur;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.ROBitSet;
import ru.csc.vindur.document.Document;
import ru.csc.vindur.executor.Executor;
import ru.csc.vindur.executor.Plan;
import ru.csc.vindur.executor.Step;
import ru.csc.vindur.storage.StorageBase;
import ru.csc.vindur.storage.StorageHelper;
import ru.csc.vindur.storage.StorageType;

/**
 * Created by Pavel Chursin on 05.10.2014.
 */
@SuppressWarnings("rawtypes")
public class Engine {
    private static final StorageType DEFAULT_STORAGE_TYPE = StorageType.STRING;
    private final AtomicInteger documentsSequence = new AtomicInteger(0);
    private final ConcurrentMap<String, StorageBase> columns = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, Document> documents = new ConcurrentHashMap<>();
    private final EngineConfig config;

    public Engine(EngineConfig config) {
        this.config = config;
    }

    public ConcurrentMap<Integer, Document> getDocuments() {
        return documents;
    }

    public ConcurrentMap<String, StorageBase> getColumns()
    {
        return columns;
    }

    public int createDocument() {
        int nextId = documentsSequence.incrementAndGet();
        Document document = new Document(nextId);
        documents.put(document.getId(), document);
        return document.getId();
    }

    @SuppressWarnings("unchecked")
    public void setAttributeByDocId(int docId, String attribute, Object value) {
        if (!documents.containsKey(docId)) {
            throw new IllegalArgumentException("There is no such document");
        }

        StorageBase storage = findStorageBase(attribute);
        if (!storage.validateValueType(value)) {
            throw new IllegalArgumentException("Invalid value type "
                    + value.getClass().getName() + " for StorageBase "
                    + storage.getClass().getName());
        }
        documents.get(docId).setAttribute(attribute, value);
        storage.add(docId, value);
    }

    /**
     * А если storage нету, то создаем новый
     *
     * @param attribute
     * @return
     */
    private StorageBase findStorageBase(String attribute) {
        StorageBase storage;
        storage = columns.get(attribute);
        if (storage == null) {
            StorageType type = config.getValueType(attribute);
            if (type == null)
                type = DEFAULT_STORAGE_TYPE;
            StorageBase newStorageBase = StorageHelper.getColumn(type,
                    config.getBitSetSupplier());
            columns.put(attribute, newStorageBase);
            storage = newStorageBase;
        }
        return storage;
    }

    public List<Integer> executeQuery(Query query) {
        checkQuery(query);
        Executor executor = this.config.getExecutor();
        BitSet resultSet = executor.execute(query, this);
        if (resultSet == null) {
            return Collections.emptyList();
        } else {
            return resultSet.toIntList();
        }
    }

    private void checkQuery(Query query) throws IllegalArgumentException {
        for (Entry<String, Object> part : query.getQueryParts().entrySet()) {
            StorageBase storage = columns.get(part.getKey());
            if (storage == null) {
                throw new IllegalArgumentException("StorageBase for attribute "
                        + part.getKey() + " is not created");
            }
            if (!storage.validateRequestType(part.getValue())) {
                throw new IllegalArgumentException("StorageBase "
                        + storage.getClass().getName() + " for attribute "
                        + part.getKey() + " is uncompatible with query "
                        + part.getValue().getClass().getName());
            }
        }
    }
}