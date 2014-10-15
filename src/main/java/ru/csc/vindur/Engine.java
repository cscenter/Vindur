package ru.csc.vindur;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Pavel Chursin on 05.10.2014.
 */
public class Engine {
    public static final BitSet NOTHING = new BitSet(1);

    private int expectedVolume;
    private AtomicInteger documentsSequence;

    private RequestOptimizer requestOptimizer;
    private Map<String, IColumn> columns;
    private Map<Integer, Document> documents;

    public Engine(EngineConfig config) {
        documentsSequence = new AtomicInteger(0);
        expectedVolume = config.getExpectedVolume();
        columns = new HashMap<>();
        documents = new HashMap<>(expectedVolume);
        requestOptimizer = new RequestOptimizer();

        for(String attribute : config.getAttributes()) {
            addColumn(
                    attribute,
                    ColumnHelper.getColumn(config.getValueType(attribute), expectedVolume)
            );
        }
    }

    public void addColumn(String attribute, IColumn column) {
        if (columns.containsKey(attribute))
            throw new IllegalArgumentException("Column for this attribute already exists");
        columns.put(attribute, column);
    }

    public int createDocument() {
        Document document = Document.nextDocument(documentsSequence);
        documents.put(document.getId(), document);
        return document.getId();
    }

    public void setAttributeByDocId(int id, String attribute, Value value) {
        if (!documents.containsKey(id))
            throw new IllegalArgumentException("There is no such document");
        documents.get(id).setAttribute(attribute, value);
        columns.get(attribute).add(id, value);
    }

    public ArrayList<Integer> executeRequest(Request request) {
        request = requestOptimizer.optimize(request);
        BitSet resultSet = null;
        for (Request.RequestPart requestPart : request.getRequestParts()) {
            if (resultSet == null)
                resultSet = executeRequestPart(requestPart);
            else
                resultSet.and(executeRequestPart(requestPart));
        }

        ArrayList<Integer> result = new ArrayList<>();
        if (resultSet == null)
            return result;
        for(int docId = resultSet.nextSetBit(0); docId != -1; docId = resultSet.nextSetBit(docId + 1)) {
            result.add(docId);
        }
        return result;
    }

    private BitSet executeRequestPart(Request.RequestPart requestPart) {
        IColumn index = columns.get(requestPart.tag);
        if (index == null)
            return NOTHING;

        //TODO range request
        if (requestPart.isExact) {
            return index.findSet(requestPart.from);
        }

        return NOTHING;
    }

    private class RequestOptimizer {
        public Request optimize(Request request) {
            return request;
        }
    }
}
