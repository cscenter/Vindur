package ru.csc.vindur;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.bitset.ROBitSet;
import ru.csc.vindur.document.Document;
import ru.csc.vindur.document.StorageType;
import ru.csc.vindur.document.Value;
import ru.csc.vindur.optimizer.Plan;
import ru.csc.vindur.optimizer.Step;
import ru.csc.vindur.storage.RangeStorage;
import ru.csc.vindur.storage.Storage;
import ru.csc.vindur.storage.StorageHelper;

/**
 * Created by Pavel Chursin on 05.10.2014.
 */
@ThreadSafe
public class Engine
{
	private static final Logger LOG = LoggerFactory.getLogger(Engine.class);
    private static final StorageType DEFAULT_STORAGE_TYPE = StorageType.STRING;
	private final AtomicInteger documentsSequence = new AtomicInteger(0);
    private final ConcurrentMap<String, Storage> columns = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, Document> documents = new ConcurrentHashMap<>();
	private final EngineConfig config;

    public Engine(EngineConfig config) {
    	this.config = config;
    }

    public int createDocument()
    {
        int nextId = documentsSequence.incrementAndGet();
        Document document = new Document(nextId);
        documents.put(document.getId(), document);
        return document.getId();
    }

    public void setAttributeByDocId(int docId, String attribute, Value value) {
        if (!documents.containsKey(docId)) {
            throw new IllegalArgumentException("There is no such document");
        }
        
        Storage storage = findStorage(attribute);
        documents.get(docId).setAttribute(attribute, value);
        storage.add(docId, value);
    }

    /**
     * А если storage нету, то создаем новый
     * @param attribute
     * @return
     */
	private Storage findStorage(String attribute)
    {
		Storage storage;
        storage = columns.get(attribute);
        if (storage==null)
        {
            StorageType type = config.getValueType(attribute);
            if (type==null) type=DEFAULT_STORAGE_TYPE;
            Storage newStorage = StorageHelper.getColumn(type, config.getBitSetFabric());
            columns.put(attribute,newStorage);
            storage = newStorage;
        }
        return storage;
	}
	
    public List<Integer> executeRequest(Request request) throws Exception
    {
        Plan plan = config.getOptimizer().generatePlan(request, this);

        Step step = plan.next();
        BitSet resultSet = null;
        try {
	        while (step != null)
	        {
				resultSet = executeStep(step, resultSet);
	            config.getOptimizer().updatePlan(plan, resultSet.cardinality());
	            if (resultSet.cardinality() == 0)
	                return Collections.emptyList();
	            step = plan.next();
	        }
		} catch (Exception e) {
			LOG.debug("Exception when executing request. Message: {}", e.getMessage());
			throw e;
		}
        if (resultSet == null)
            return Collections.emptyList();

        return resultSet.toIntList();
    }

    public BitSet executeStep(Step step, BitSet currentResultSet) throws Exception
    {
        //todo добавить проверки на соответствие шагов и storage. Увы, в оптимизатор не вытащить (
        Storage index = findStorage(step.getStorageName());
        ROBitSet r = null;
        switch (step.getType())
        {
            case EXACT: r = index.findSet(step.getFrom()); break;
            case RANGE: r = ((RangeStorage) index).findRangeSet(step.getFrom(), step.getTo());
        }
        if (currentResultSet == null) return r.copy(); //копия первого запроса, на нее будем накладывать фильтры
        return currentResultSet.and(r);
    }

    public Storage getStorage(String attribute)
    {
        return findStorage(attribute);
    }
}