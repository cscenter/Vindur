package ru.csc.vindur;

import ru.csc.vindur.entity.Entity;
import ru.csc.vindur.entity.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pavel Chursin on 05.10.2014.
 */
public class Document {
    private Map<String, ArrayList<Value>> vals = new HashMap<>();  // aspect -> values

    private int id;

    public Document(int id) {
        this.id = id;
    }

    public void loadEntity(Entity entity) {
        this.vals = entity.getValues();
    }

    public void registerValue(String attribute, Value value) {
        if (!vals.containsKey(attribute)) {
            vals.put(attribute, new ArrayList<Value>());
        }
        vals.get(attribute).add(value);
    }

    public int getId() {
        return id;
    }

    public Map<String, ArrayList<Value>> getVals() {
        return vals;
    }
}
