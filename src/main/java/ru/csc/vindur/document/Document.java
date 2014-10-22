package ru.csc.vindur.document;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Pavel Chursin on 05.10.2014.
 */
public class Document {
    private final Map<String, List<Value>> vals = new HashMap<>();  // aspect -> values
    private final int id;

    private Document(int id) {
        this.id = id;
    }

    // skrivohatskiy TODO: find out is it a good method
    public static Document nextDocument(AtomicInteger docSequence) {
        return new Document(docSequence.incrementAndGet());
    }

    public void setAttribute(String attribute, Value value) {
        if (!vals.containsKey(attribute)) {
            vals.put(attribute, new ArrayList<Value>());
        }
        vals.get(attribute).add(value);
    }

    public int getId() {
        return id;
    }

    public List<Value> getValues(String attribute) {
        return vals.get(attribute);
    }

    public Set<String> getAttributes() {
        return vals.keySet();
    }
}
