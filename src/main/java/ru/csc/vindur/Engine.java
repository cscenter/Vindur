package ru.csc.vindur;

import ru.csc.vindur.entity.Value;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pavel Chursin on 05.10.2014.
 */
public class Engine {
    int expectedVolume;
    int documentsSequence;
    RequestOptimizer requestOptimizer;
    Map<String, IIndex> indexes;
    Map<Integer, Document> documents;

    public Engine(int expectedVolume) {
        documentsSequence = 0;
        this.expectedVolume = expectedVolume;
        indexes = new HashMap<>();
        documents = new HashMap<>(expectedVolume);
        requestOptimizer = new RequestOptimizer();
    }

    public void addIndex(String attribute, IIndex index) {
        if (indexes.containsKey(attribute))
            throw new IllegalArgumentException("This attribute already has index");
        indexes.put(attribute, index);
    }

    public void removeIndex(String attribute) {
        indexes.remove(attribute);
    }

    public int createDocument() {
        Document document = new Document(documentsSequence);
        documents.put(documentsSequence, document);
        return documentsSequence++;
    }

    public void clearDocumentById(int id) {
        Document document = documents.get(id);
        if (document == null)
            return;

        for (String attribute : document.getVals().keySet()) {
            IIndex index = indexes.get(attribute);
            for (Value value : document.getVals().get(attribute)) {
                index.remove(id, value);
            }
        }
        document.getVals().clear();
    }

    public void addValueByDocId(int id, String attribute, Value value) {
        if (!documents.containsKey(id))
            throw new IllegalArgumentException("There is no such document");
        documents.get(id).registerValue(attribute, value);
        indexes.get(attribute).add(id, value);
    }

    public void addValuesListByDocId(int id, String attribute, ArrayList<Value> values) {
        for (Value value : values) {
            addValueByDocId(id, attribute, value);
        }
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
        IIndex index = indexes.get(requestPart.tag);
        if (index == null)
            return new BitSet(1);

        //TODO range request
        if (requestPart.isExact) {
            return index.findSet(requestPart.from);
        }

        return new BitSet(1);
    }

    private class RequestOptimizer {
        public Request optimize(Request request) {
            return request;
        }
    }
}
