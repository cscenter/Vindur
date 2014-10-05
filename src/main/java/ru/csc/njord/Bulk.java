package ru.csc.njord;

import ru.csc.njord.entity.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author: Phillip Delgyado
* Date: 07.03.14 5:31
*/ /* ***************************************************************************************** */
public class Bulk
{
    Integer documentId;
    Map<String,List<Value>> values; //aspect->value

    public Bulk(Integer documentId)
    {
        this.documentId = documentId;
        this.values = new HashMap<>();
    }

    public void registerValue(Integer docId, String aspect, Value value)
    {
        List<Value> vv;
        vv = values.get(aspect);
        if (vv==null)
        {
           vv = new ArrayList<>();
           values.put(aspect,vv);
        }
        vv.add(value);
    }
}
