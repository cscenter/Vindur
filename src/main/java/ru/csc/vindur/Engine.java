package ru.csc.vindur;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import ru.csc.vindur.bitset.BitArray;
import ru.csc.vindur.document.Document;
import ru.csc.vindur.executor.DumbExecutor;
import ru.csc.vindur.executor.Executor;
import ru.csc.vindur.storage.Storage;

public class Engine
{
    private final AtomicInteger documentsSequence = new AtomicInteger(0);
    private final Map<Integer, Document> documents = new ConcurrentHashMap<>();
    private Map<String, Storage> storages;
    private Executor executor;

    private Engine()
    {
        this.executor = new DumbExecutor();
        this.storages = new HashMap<>();
    }

    public Document getDocument(Integer id) {
        return documents.get(id);
    }
    public int getDocumentCount() {return documents.size();};

    //todo refactor to getStorage(key)
    public Map<String, Storage> getStorages() {
        return storages;
    }

    public int createDocument() {
        int nextId = documentsSequence.getAndIncrement();
        Document document = new Document(nextId);
        documents.put(document.getId(), document);
        return document.getId();
    }

    @SuppressWarnings("unchecked")
    public void setAttributeByDocId(int docId, String attribute, Object value) {
        if (!documents.containsKey(docId)) {
            throw new IllegalArgumentException("There is no such document");
        }

        Storage storage = findStorageBase(attribute);
        if (!storage.validateValueType(value)) {
            throw new IllegalArgumentException("Invalid value type "
                    + value.getClass().getName() + " for StorageBase "
                    + storage.getClass().getName());
        }
        documents.get(docId).setAttribute(attribute, value);
        storage.add(docId, value);
    }


    //todo по умолчанию создавать ExactStorage(String)
    /**
     * А если storage нету, то создаем новый
     *
     * @param attribute
     * @return
     */
    private Storage findStorageBase(String attribute) {
        Storage storage;
        storage = storages.get(attribute);
        if (storage == null) {
            throw new IllegalArgumentException(
                    "There is no storage for attribute " + attribute);
        }
        return storage;
    }

    public List<Integer> executeQuery(Query query) {
        checkQuery(query);
        BitArray resultSet = executor.execute(query, this);
        if (resultSet == null) {
            return Collections.emptyList();
        } else {
            return resultSet.toIntList();
        }
    }

    private void checkQuery(Query query) throws IllegalArgumentException
    {
        for (Entry<String, Object> part : query.getQueryParts().entrySet()) {
            Storage storage = storages.get(part.getKey());
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

    public static Builder build()
    {
        return new Builder();
    }


    public static class Builder
    {
        private Engine engine;

        public Builder()
        {
            this.engine = new Engine();
        }

        public Builder executor(Executor e)
        {
           engine.executor=e;
           return this;
        }

        public Builder storage(String name, Storage s)
        {
            engine.storages.put(name,s);
            return this;
        }

        public Engine init()
        {
            return engine;
        }

    }
}