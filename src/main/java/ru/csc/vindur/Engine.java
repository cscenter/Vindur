package ru.csc.vindur;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.document.Document;
import ru.csc.vindur.executor.DumbExecutor;
import ru.csc.vindur.executor.Executor;
import ru.csc.vindur.storage.StorageBase;
import ru.csc.vindur.storage.StorageHelper;
import ru.csc.vindur.storage.StorageType;

/**
 * Created by Pavel Chursin on 05.10.2014.
 */
@SuppressWarnings("rawtypes")
public class Engine {
    private final AtomicInteger documentsSequence = new AtomicInteger(0);
    private final ConcurrentMap<Integer, Document> documents = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, StorageBase> columns;
    private final Executor executor;

    private Engine(Executor executor,
            ConcurrentMap<String, StorageBase> storages) {
        this.executor = executor;
        this.columns = storages;
    }

    public ConcurrentMap<Integer, Document> getDocuments() {
        return documents;
    }

    public ConcurrentMap<String, StorageBase> getColumns() {
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
            throw new IllegalArgumentException(
                    "There is no storage for attribute " + attribute);
        }
        return storage;
    }

    public List<Integer> executeQuery(Query query) {
        checkQuery(query);
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
                        + part.getKey() + " is incompatible with query "
                        + part.getValue().getClass().getName());
            }
        }
    }

    public static class EngineBuilder {
        private final static Executor DEFAULT_EXECUTOR = new DumbExecutor();
        private final ConcurrentMap<String, StorageBase> columns = new ConcurrentHashMap<>();
        private Executor executor = null;
        private boolean engineBuilded = false;
        private final Supplier<BitSet> bitSetSupplier;

        public EngineBuilder(Supplier<BitSet> bitSetSupplier) {
            this.bitSetSupplier = bitSetSupplier;
        }

        public EngineBuilder setExecutor(Executor executor) {
            checkForBuilded();
            this.executor = executor;
            return this;
        }

        public EngineBuilder setStorage(String atributeName,
                StorageType storageType) {
            checkForBuilded();
            putStorageNotCheck(atributeName, storageType);
            return this;
        }

        /**
         * Storage should return the same bit set as the other
         * storages(Specified in the Builder constructor)
         * 
         * @param atributeName
         * @param storage
         * @return this
         */
        public EngineBuilder setUserStorage(String atributeName,
                StorageBase storage) {
            checkForBuilded();
            columns.put(atributeName, storage);
            return this;
        }

        private void putStorageNotCheck(String atributeName,
                StorageType storageType) {
            columns.put(atributeName,
                    StorageHelper.getColumn(storageType, bitSetSupplier));
        }

        public EngineBuilder setStorages(Map<String, StorageType> indexes) {
            checkForBuilded();
            for (Entry<String, StorageType> storage : indexes.entrySet()) {
                putStorageNotCheck(storage.getKey(), storage.getValue());
            }
            return this;
        }

        public Engine createEngine() {
            engineBuilded = true;
            executor = executor == null ? DEFAULT_EXECUTOR : executor;
            return new Engine(executor, columns);
        }

        private void checkForBuilded() {
            if (engineBuilded) {
                throw new IllegalStateException(
                        "This builder was already used to create an engine");
            }
        }
    }
}