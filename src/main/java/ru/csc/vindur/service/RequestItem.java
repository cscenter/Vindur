package ru.csc.vindur.service;

/**
 * @author Andrey Kokorev
 *         Created on 16.04.2015.
 */
public class RequestItem
{
    private String attribute;
    private Object from;
    private Object to;

    public String getAttribute()
    {
        return attribute;
    }

    public void setAttribute(String attribute)
    {
        this.attribute = attribute;
    }

    public Object getFrom()
    {
        return from;
    }

    public void setFrom(Object from)
    {
        this.from = from;
    }

    public Object getTo()
    {
        return to;
    }

    public void setTo(Object to)
    {
        this.to = to;
    }

    public String toString()
    {
        return "{Attribute: " + attribute + ", from: " + from + ", to: " + to + "}";
    }

}
