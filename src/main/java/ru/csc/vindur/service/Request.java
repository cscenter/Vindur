package ru.csc.vindur.service;

import java.util.List;
import java.util.Map;

/**
 * @author Andrey Kokorev
 *         Created on 16.04.2015.
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
