package ru.csc.vindur;

import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import ru.csc.vindur.Request.RequestPart;
import ru.csc.vindur.bitset.BitSetUtils;
import ru.csc.vindur.column.ColumnHelper;
import ru.csc.vindur.column.Column;
import ru.csc.vindur.document.Document;
import ru.csc.vindur.document.Value;

/**
 * Created by Pavel Chursin on 05.10.2014.
 */
public class Engine {
    private final AtomicInteger documentsSequence = new AtomicInteger(0);
    private final Map<String, Column> columns = new HashMap<>();
    private final Map<Integer, Document> documents = new HashMap<>();

    public Engine(EngineConfig config) {
        for(String attribute : config.getAttributes()) {
            addColumn(
                    attribute,
                    ColumnHelper.getColumn(config.getValueType(attribute))
            );
        }
    }

    public void addColumn(String attribute, Column column) {
        if (columns.containsKey(attribute)) {
            throw new IllegalArgumentException("Column for this attribute already exists");
        }
        columns.put(attribute, column);
    }

    public int createDocument() {
        Document document = Document.nextDocument(documentsSequence);
        documents.put(document.getId(), document);
        return document.getId();
    }

    public void setAttributeByDocId(int id, String attribute, Value value) {
        if (!documents.containsKey(id)) {
            throw new IllegalArgumentException("There is no such document");
        }
        if(!columns.containsKey(attribute)) {
            throw new IllegalArgumentException("There is no such column");
        }
        documents.get(id).setAttribute(attribute, value);
        columns.get(attribute).add(id, value);
    }

    public List<Integer> executeRequest(Request request) {
        BitSet resultSet = null;
        for (RequestPart requestPart : request.getRequestParts()) {
            if (resultSet == null) {
                resultSet = executeRequestPart(requestPart);
            } else {
                resultSet.and(executeRequestPart(requestPart));
            }

            if (resultSet.cardinality() == 0) {
            	return Collections.emptyList();
            }
        }

        if (resultSet == null) {
        	return Collections.emptyList();
        }
        
        return BitSetUtils.bitSetToArrayList(resultSet);
    }

    private BitSet executeRequestPart(Request.RequestPart requestPart) {
        Column index = columns.get(requestPart.tag);
        if (index == null) {
            return BitSetUtils.EMPTY_BITSET;
        }

        //TODO range request
        if (requestPart.isExact) {
            return index.findSet(requestPart.from);
        }

        return BitSetUtils.EMPTY_BITSET;
    }
}