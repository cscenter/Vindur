package ru.csc.vindur;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
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
import ru.csc.vindur.storage.RangeStorage;
import ru.csc.vindur.storage.Storage;
import ru.csc.vindur.storage.StorageHelper;

/**
 * Created by Pavel Chursin on 05.10.2014.
 */
@ThreadSafe
public class Engine {
	private static final Logger LOG = LoggerFactory.getLogger(Engine.class);
    private static final StorageType DEFAULT_VALUE_TYPE = StorageType.STRING;
	private final AtomicInteger documentsSequence = new AtomicInteger(0);
    private final ConcurrentMap<String, Storage> columns = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, Document> documents = new ConcurrentHashMap<>();
	private final EngineConfig config;

    public Engine(EngineConfig config) {
    	this.config = config;
    }

    public int createDocument() {
        Document document = Document.nextDocument(documentsSequence);
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

	private Storage createStorage(String attribute) {
		Storage storage;
		StorageType type = config.getValueType(attribute);
		synchronized (this) {
			storage = columns.get(attribute);
			if(storage != null) {
				return storage;
			}
			if(type == null) {
				LOG.warn("Default value type({}) used for attribute {}", DEFAULT_VALUE_TYPE, attribute);
				type = DEFAULT_VALUE_TYPE;
			}
			storage = StorageHelper.getColumn(type, config.getBitSetFabric());
			columns.put(attribute, storage);
		}
		return storage;
	}

    @Deprecated //асинхронность в списке запросов - не дело движка, можно и снаружи обернуть
	public Future<List<Integer>> executeRequestAsync(Request request) {
		return config.getExecutorService().submit(new RequestCallable(request));
	}
	
    public List<Integer> executeRequest(Request request)
    {
        BitSet resultSet = null;
        for (RequestPart requestPart : request.getRequestParts())
        {
            if (resultSet == null)
                resultSet = executeRequestPart(requestPart);
             else
                resultSet = resultSet.and(executeRequestPart(requestPart));

            if (resultSet.cardinality() == 0)
                return Collections.emptyList();
        }

        if (resultSet == null)
            return Collections.emptyList();

        return resultSet.toIntList();

    }

    @Deprecated //не нужно
    private class RequestCallable implements Callable<List<Integer>>{

		private final Request request;

		public RequestCallable(Request request) {
			this.request = request;
		}

		@Override
		public List<Integer> call() {
	        BitSet resultSet = null;
	        for (RequestPart requestPart : request.getRequestParts()) {
	            if (resultSet == null) {
	                resultSet = executeRequestPart(requestPart);
	            } else {
	            	resultSet = resultSet.and(executeRequestPart(requestPart));
	            }

	            if (resultSet.cardinality() == 0) {
	            	return Collections.emptyList();
	            }
	        }

	        if (resultSet == null) {
	        	return Collections.emptyList();
	        }
	        
	        return resultSet.toIntList();
		}


    }

    private BitSet executeRequestPart(Request.RequestPart requestPart)
    {
        Storage index = columns.get(requestPart.tag);
        if (index == null)
        {
            LOG.warn("Index for requested attribute {} is not created", requestPart.tag);
            return config.getBitSetFabric().newInstance();
        }

        if (requestPart.isExact)
        {
            return index.findSet(requestPart.from);
        } else {
            if (!(index instanceof RangeStorage)) {
                throw new UnsupportedOperationException(
                        String.format("Storage '%s' does not support range requests", requestPart.tag)
                );
            }
            return ((RangeStorage) index).findRangeSet(requestPart.from, requestPart.to);
        }
    }


}