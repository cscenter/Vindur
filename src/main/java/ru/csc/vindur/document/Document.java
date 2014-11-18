package ru.csc.vindur.document;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Created by Pavel Chursin on 05.10.2014.
 */
@ThreadSafe
public class Document
{
    private final ConcurrentMap<String, List<Value>> vals = new ConcurrentHashMap<>();  // attribute -> values
    private final int id;

    public Document(int id) {
        this.id = id;
    }


    // TODO Why do we need this method? 
    public void setAttribute(String attribute, Value value) {
        List<Value> values = vals.get(attribute);
    	if (values == null) {
    		// Contains Double checked locking inside
    		values = createValues(attribute);
        }
    	synchronized (values) {
    		// TODO maybe change List to CopyOnWriteList or something else
            values.add(value);
		}
    }

	private List<Value> createValues(String attribute) {
		List<Value> values;

		synchronized (this) {
			values = vals.get(attribute);
			if(values != null) {
				return values;
			}
			values = new ArrayList<Value>();
			vals.put(attribute, values);
		}
		return values;
	}

	public boolean valueIsPresentByAttribute(String attribute, Value value)
	{
		List<Value> values = vals.get(attribute);
		if (values != null)
		{
			return values.contains(value);
		}
		else
		{
			return false;
		}
	}

	public boolean valueIsInRangeByAttribute(String attribute, Value from, Value to)
	{
		List<Value> values = vals.get(attribute);
		for (Value value : values)
		{
			//<shitcode!>
			//todo: how to compare????
			if ((value.getValue().compareTo(to.getValue()) < 0) && (value.getValue().compareTo(from.getValue()) > 0))
			{
				return true;
			}
			//</shitcode>
		}
		return false;
	}

    public int getId() {
        return id;
    }
}
