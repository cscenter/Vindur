package ru.csc.vindur;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;

import ru.csc.vindur.bitset.BitArray;
import ru.csc.vindur.document.Document;
import ru.csc.vindur.executor.DumbExecutor;
import ru.csc.vindur.executor.Executor;
import ru.csc.vindur.executor.TunableExecutor;
import ru.csc.vindur.executor.tuner.Tuner;
import ru.csc.vindur.storage.Storage;

@SuppressWarnings("rawtypes")
public class Engine
{
    private final AtomicInteger documentsSequence = new AtomicInteger(0);
    private final Map<Integer, Document> documents = new ConcurrentHashMap<>();
	private Map<String, Storage> storages;
    private Executor executor;
    private Tuner tuner = new Tuner(this);

    private Engine()
    {
        this.executor = new DumbExecutor();
        this.storages = new HashMap<>();
        Thread worker = new Thread( () -> { while (true) {
            try {
                tuner.call();
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }});
    }

    /**
     * @param id document id
     * @return document with given {@code id}
     */
    public Document getDocument(Integer id) {
        return documents.get(id);
    }

    /**
     * @return created documents count
     */
    public int getDocumentCount() {return documents.size();}

    //todo refactor to getStorage(key)
    public Map<String, Storage> getStorages() {
        return storages;
    }


    /**
     * Create new document and get unique document id, which is used to access document
     * @return id of created document
     */
    public int createDocument() {
        int nextId = documentsSequence.getAndIncrement();
        Document document = new Document(nextId);
        documents.put(document.getId(), document);
        return document.getId();
    }

    /**
     * Set value of specified attribute in document by given ID
     * @param docId document id
     * @param attribute attribute name
     * @param value value to be set
     */
    @SuppressWarnings("unchecked")
    public void setValue(int docId, String attribute, Object value) {
        //заменить на transactinManager.setValue(...)


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
     * Get storage by attribute name
     * @param attribute attribute name
     * @return storage, which is belong to specified attribute
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

    /**
     * @param query - query to execute
     * @return list of document ID's, which satisfy to specified query
     */
    public List<Integer> executeQuery(Query query) {
        //transactionManager.prepareQuery(query)
        checkQuery(query);
        BitArray resultSet = executor.execute(query, this);
        if (resultSet == null) {
            return Collections.emptyList();
        } else {
            return resultSet.toIntList();
        }
        //transactionManager.checkQuery(resultSet,query)
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

    public Tuner getTuner()
    {
        return tuner;
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