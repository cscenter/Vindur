package ru.csc.vindur;

import java.util.*;
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

    public List<Integer> executeRequest(Request request) {
        Map<RequestPlan.PlanElement, Integer> cardinalityList = new HashMap<>();

        for (Request.RequestPart requestPart : request.getRequestParts()) {
            BitSet resultSetPart = executeRequestPart(requestPart);
            int resultSetCardinality = resultSetPart.cardinality();
            RequestPlan.PlanElement planElement = new RequestPlan.PlanElement(requestPart);
            cardinalityList.put(planElement, resultSetCardinality);
        }

        ValueComparator valueComparator = new ValueComparator(cardinalityList);
        SortedMap<RequestPlan.PlanElement, Integer> sortedCardinalityList = new TreeMap<>(valueComparator);
        sortedCardinalityList.putAll(cardinalityList);


        BitSet resultSet = null;
        for (Map.Entry<RequestPlan.PlanElement, Integer> entry : sortedCardinalityList.entrySet()) {
            if (resultSet == null)
                resultSet = executeRequestPart(entry.getKey().getRequestPart());
            else
                resultSet.and(executeRequestPart(entry.getKey().getRequestPart()));

            if (resultSet.cardinality() == 0) break;

        }

        if (resultSet == null || resultSet.cardinality() == 0)
            return Collections.emptyList();
        ArrayList<Integer> result = new ArrayList<>(resultSet.cardinality());
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

    private class ValueComparator implements Comparator<RequestPlan.PlanElement> {

        Map<RequestPlan.PlanElement, Integer> map;

        public ValueComparator(Map<RequestPlan.PlanElement, Integer> base) {
            this.map = base;
        }

        @Override
        public int compare(RequestPlan.PlanElement o1, RequestPlan.PlanElement o2) {
            if (map.get(o1) >= map.get(o2)) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    private class RequestOptimizer {
        public Request optimize(Request request) {
            return request;
        }
    }
}