package ru.csc.vindur.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Created by Pavel Chursin on 05.10.2014.
 */
@ThreadSafe
public class Document {
    private final ConcurrentMap<String, List<Object>> vals = new ConcurrentHashMap<>(); // attribute
                                                                                        // ->
                                                                                        // values
    private final int id;

    public Document(int id) {
        this.id = id;
    }

    public void setAttribute(String attribute, Object value)
    {
        List<Object> values = vals.get(attribute);
        if (values == null)
            values = createValues(attribute);
        values.add(value);
    }

    private List<Object> createValues(String attribute)
    {
        List<Object> values;

            values = vals.get(attribute);
            if (values != null)
                return values;
            values = new ArrayList<>();
            vals.put(attribute, values);
        return values;
    }

    public int getId() {
        return id;
    }

    public List<Object> getValues(String attribute) {
        return vals.get(attribute);
    }

    public Set<String> getAttributes() {return vals.keySet(); }


}
