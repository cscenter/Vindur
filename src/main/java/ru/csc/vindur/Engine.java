package ru.csc.vindur;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.csc.vindur.Request.RequestPart;
import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.document.Document;
import ru.csc.vindur.document.StorageType;
import ru.csc.vindur.document.Value;
import ru.csc.vindur.optimizer.Optimizer;
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
        
        Storage storage = columns.get(attribute);

        if(storage == null) {
    		// Contains Double checked locking inside
        	storage = createStorage(attribute);
        }
        documents.get(docId).setAttribute(attribute, value);
        storage.add(docId, value);
    }

	private Storage createStorage(String attribute)
    {
		Storage storage;
		StorageType type = config.getValueType(attribute);
		synchronized (this) {
			storage = columns.get(attribute);
			if(storage != null) {
				return storage;
			}
			if(type == null) {
				LOG.warn("Default value type({}) used for attribute {}", DEFAULT_STORAGE_TYPE, attribute);
				type = DEFAULT_STORAGE_TYPE;
			}
			storage = StorageHelper.getColumn(type, config.getBitSetFabric());
			columns.put(attribute, storage);
		}
		return storage;
	}
	
    public List<Integer> executeRequest(Request request)
    {
        BitSet resultSet;
        Optimizer optimizer = this.config.getOptimizer();
        Plan plan = optimizer.generatePlan(request, this);

        Step step = plan.next();
        resultSet = executeStep(step, null);
        while (step != null) {
            resultSet = (executeStep(step, resultSet));
            optimizer.updatePlan(plan, resultSet.cardinality());
            if (resultSet.cardinality() == 0)
                return Collections.emptyList();
            step = plan.next();
        }

        if (resultSet == null)
            return Collections.emptyList();

        return resultSet.toIntList();
    }

    public BitSet executeStep(Step step, BitSet currentResultSet) {
        Storage index = columns.get(step.getStorageName());
        if (index == null) {
            LOG.warn("Index for requested attribute {} is not created", step.getStorageName());
            return config.getBitSetFabric().newInstance();
        }

        if (step.getType() == Step.Type.EXACT) {
            //todo: check if currentResultSet is null, if yes - return findSet, else - return findSet AND currentResultSet
            if (currentResultSet == null) {
                return index.findSet(step.getFrom());
            } else {
                return currentResultSet.and(index.findSet(step.getFrom()));
            }
        } else {
            if (!(index instanceof RangeStorage))
                throw new UnsupportedOperationException(String.format("Storage '%s' does not support range requests", step.getStorageName()));
            if (currentResultSet == null) {
                return ((RangeStorage) index).findRangeSet(step.getFrom(), step.getTo());
            } else {
                return currentResultSet.and(((RangeStorage) index).findRangeSet(step.getFrom(), step.getTo()));
            }
        }
    }

    public Storage getStorage(String key) {
        return columns.get(key);
    }
}