package ru.csc.njord.entity;

import java.util.*;

/**
 * @author Andrey Kokorev
 * Created on 25.09.2014.
 */
public class Entity {
    private Map<String, ArrayList<Value>> vals = new HashMap<>();  // aspect -> values
    private String ID;

    public Entity(String id) {
        ID = id;
    }

    public Map<String, ArrayList<Value>> getValues() {
        return vals;
    }

    public String getId() {
        return ID;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        sb.append("Entity(ID = " + ID + ") {");
        for(String key: vals.keySet()) {
            if(!first) sb.append(", ");
            else first = false;

            Collection<Value> valsByKey = vals.get(key);

            boolean firstInner = true;

            sb.append(key + ": ");
            if(valsByKey.size() != 1) sb.append('{');
            for(Value v: valsByKey) {
                if(!firstInner) sb.append(", ");
                else firstInner = false;

                sb.append("'");
                sb.append(v.toString());
                sb.append("'");
            }
            if(valsByKey.size() != 1) sb.append('}');
        }
        sb.append("}");

        return sb.toString();
    }
}
