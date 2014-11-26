package ru.csc.vindur;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Phillip Delgyado
 */
public class Request
{
    private final Map<String, Object> reqs = new HashMap<>(); //aspect->Request

    public static Request build()
    {
        return new Request();
    }

    public Request request(String attribute, Object storageRequest) {
    	reqs.put(attribute, storageRequest);
    	return this;
    }

    public Map<String, Object> getRequestParts()
    {
        return reqs;
    }
}