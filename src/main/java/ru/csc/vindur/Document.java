package ru.csc.vindur;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Pavel Chursin on 05.10.2014.
 */
public class Document {
    private Map<String, List<Value>> vals = new HashMap<>();  // aspect -> values
    private int id;

    private Document(int id) {
        this.id = id;
    }

    static Document nextDocument(AtomicInteger docSequence) {
        return new Document(docSequence.incrementAndGet());
    }

    void setAttribute(String attribute, Value value) {
        if (!vals.containsKey(attribute)) {
            vals.put(attribute, new ArrayList<Value>());
        }
        vals.get(attribute).add(value);
    }

    int getId() {
        return id;
    }

    List<Value> getValues(String attribute) {
        return vals.get(attribute);
    }

    Set<String> getAttributes() {
        return vals.keySet();
    }
}
