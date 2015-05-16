package ru.csc.vindur;

import javafx.util.Pair;
import ru.csc.vindur.bitset.BitArray;
import ru.csc.vindur.document.Document;
import ru.csc.vindur.executor.Executor;
import ru.csc.vindur.executor.TinyExecutor;
import ru.csc.vindur.executor.TunableExecutor;
import ru.csc.vindur.executor.tuner.Tuner;
import ru.csc.vindur.storage.Storage;
import ru.csc.vindur.transactions.Operation;
import ru.csc.vindur.transactions.Transaction;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("rawtypes")
public class Engine
{
    private final AtomicInteger documentsSequence = new AtomicInteger(0);
    private final Map<Integer, Document> documents = new ConcurrentHashMap<>();
	private Map<String, Storage> storages;

    private Executor executor;

    private Tuner tuner;
    private volatile List<Operation> currentChanges = new LinkedList<>();
    private volatile HashMap<Pair<String, Object>, List<Integer>> uncommitedChanges = new HashMap<>(); //(attribute, value) -> list of documents
    private volatile HashMap<Long, Transaction> transactions = new HashMap<>(); // transactionID -> transaction
    private Random random = new Random();
    private Thread transactionsThread = new Thread( () -> {
        if (!uncommitedChanges.isEmpty())
        {
            List<Pair<String, Object>> keysToRemove = new ArrayList<>();
            int counter = 0;
            for (Entry<Pair<String, Object>, List<Integer>> entry: uncommitedChanges.entrySet())
            {
                String attribute = entry.getKey().getKey();
                Object value = entry.getKey().getValue();

                for (int docID : entry.getValue())
                    this.setValue(docID, attribute, value);

                keysToRemove.add(entry.getKey());

                if (counter == 1000)
                    return;
                counter++;
            }

            keysToRemove.forEach(uncommitedChanges::remove);

        }
    });

    private Engine()
    {
        this.executor = new TinyExecutor();
        this.storages = new HashMap<>();
    }
    public void setTuner(Tuner tuner) {
        this.tuner = tuner;
        Thread tunerThread = new Thread(() -> {
            while (true) {
                try {
                    tuner.call();
                    return;
                    //Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        tunerThread.setDaemon(true);
        tunerThread.start();
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


    public void setValue(long transactionId, int docId, String attribute, Object value)
    {
        Operation op = new Operation(Operation.Type.UPDATE, docId, attribute, value);
        currentChanges.add(op);
        if (transactions.get(transactionId) != null)
        {
            transactions.get(transactionId).getOperations().add(op);
        }
        else
        {
            transactions.put(transactionId, new Transaction());
            transactions.get(transactionId).getOperations().add(op);
        }
    }

    /**
     * Set value of specified attribute in document by given ID
     * @param docId document id
     * @param attribute attribute name
     * @param value value to be set
     */
    @SuppressWarnings("unchecked")
    public void setValue(int docId, String attribute, Object value) {
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
        tuner.addQuery(query);
        checkQuery(query);
        BitArray resultSet = executor.execute(query, this);
        transactionsThread.run(); //todo blockingQueue or while(true) in thread
        if (!uncommitedChanges.isEmpty())
        {
            for (Entry<Pair<String, Object>, List<Integer>> entry : uncommitedChanges.entrySet())
            {
                String attribute = entry.getKey().getKey();
                Object value = entry.getKey().getValue();
                List<Integer> docIDs = entry.getValue();

                BitArray documentsToAdd = BitArray.create();
                BitArray documentsToDel = BitArray.create();

                //пошли по всем частям в запросе
                for (Entry<String, Object> queryEntry : query.getQueryParts().entrySet())
                {
                    //пошли по всем документам, в которых значение attribute теперь равно value
                    for (int docID : docIDs)
                    {
                        //нашли документ, который теперь тоже удовлетворяет запросу - добавили
                        if (attribute.equals(queryEntry.getKey())
                                && storages.get(attribute).checkValue(docID, value, queryEntry.getValue()))
                        {
                            documentsToAdd.set(docID);
                        }

                        if (attribute.equals(queryEntry.getKey())
                                && !storages.get(attribute).checkValue(docID, value, queryEntry.getValue()))
                        {
                            documentsToDel.set(docID);
                        }
                    }

                }

                //добавили
                resultSet = resultSet.and(documentsToAdd);
                //удалили
                resultSet = resultSet.xor(documentsToDel);
            }
        }
        try {
            transactionsThread.join();
            if (resultSet == null) {
                return Collections.emptyList();
            } else {
                return resultSet.toIntList();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
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


    public long startTransaction()
    {
        Long transactionId = random.nextLong();
        transactions.put(transactionId, new Transaction());
        return transactionId;
    }

    public void commitTransaction(long transactionId)
    {
        Transaction transaction = transactions.get(transactionId);
        for (Operation op : currentChanges)
        {
            String attribute = op.attribute;
            Object value = op.value;
            int docID = op.docID;
            Pair<String, Object> key = new Pair<>(attribute, value);

            if (!uncommitedChanges.containsKey(key))
                uncommitedChanges.put(key, new LinkedList<>());

            uncommitedChanges.get(key).add(docID);
        }

        transaction.getOperations().addAll(currentChanges);
    }

    public void rollbackTransaction(long transactionId)
    {
        currentChanges.clear();
        transactions.get(transactionId).getOperations().clear();
    }

    public Tuner getTuner()
    {
        return tuner;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
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
            Tuner tuner = new Tuner(engine);
            engine.setTuner(tuner);
        }

        public Builder executor(Executor e)
        {
           engine.executor = e;
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