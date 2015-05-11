package ru.csc.vindur.service.model;

import java.util.List;

/**
 * @author Andrey Kokorev
 *         Created on 16.04.2015.
 *
 *  JSON example: {"query": [ {"attribute":"Int", "from":0, "to":10}, {"attribute":"Str", "from":"Petya"} ]}
 */
public class Request
{
    private List<RequestItem> query;

    public List<RequestItem> getQuery()
    {
        return query;
    }

    public void setQuery(List<RequestItem> query)
    {
        this.query = query;
    }
}
