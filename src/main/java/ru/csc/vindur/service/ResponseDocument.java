package ru.csc.vindur.service;

import ru.csc.vindur.document.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Andrey Kokorev
 *         Created on 16.04.2015.
 */
public class ResponseDocument
{
    private Map<String, List<Object>> values;

    public Map<String, List<Object>> getValues()
    {
        return values;
    }

    public ResponseDocument(Document document)
    {
        values = new HashMap<>();
        for(String attr : document.getAttributes())
        {
            List<Object> obj = new ArrayList<>();
            document.getValues(attr).forEach((o) -> obj.add(o));
            values.put(attr, obj);
        }
    }
}
