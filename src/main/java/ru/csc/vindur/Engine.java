package ru.csc.vindur;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.csc.vindur.Request.RequestPart;
import ru.csc.vindur.bitset.BitSet;
import ru.csc.vindur.document.Document;
import ru.csc.vindur.document.Value;
import ru.csc.vindur.document.StorageType;
import ru.csc.vindur.storage.Storage;
import ru.csc.vindur.storage.StorageHelper;

/**
 * Created by Pavel Chursin on 05.10.2014.
 */
public class Engine {
	private static final Logger LOG = LoggerFactory.getLogger(Engine.class);
    private static final StorageType DEFAULT_VALUE_TYPE = StorageType.STRING;
	private final AtomicInteger documentsSequence = new AtomicInteger(0);
    private final Map<String, Storage> columns = new HashMap<>();
    private final Map<Integer, Document> documents = new HashMap<>();
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
        	StorageType type = config.getValueType(attribute);
        	if(type == null) {
        		LOG.warn("Default value type({}) used for attribute {}", DEFAULT_VALUE_TYPE, attribute);
        		type = DEFAULT_VALUE_TYPE;
        	}
        	storage = StorageHelper.getColumn(type, config.getBitSetFabric());
        	columns.put(attribute, storage);
        }
        documents.get(docId).setAttribute(attribute, value);
        storage.add(docId, value);
    }

    public List<Integer> executeRequest(Request request) {
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

    private BitSet executeRequestPart(Request.RequestPart requestPart) {
        Storage index = columns.get(requestPart.tag);
        if (index == null) {
        	LOG.warn("Index for requested attribute {} is not created", requestPart.tag);
            return config.getBitSetFabric().newInstance();
        }

        if (requestPart.isExact) {
            return index.findSet(requestPart.from);
        }

        //TODO range request
        throw new RuntimeException("Range requests is not implemented");
    }
}