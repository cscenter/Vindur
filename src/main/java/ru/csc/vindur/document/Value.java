package ru.csc.vindur.document;

/**
 * @author Andrey Kokorev
 *         Created on 24.09.2014.
 */
public class Value
{
    private final String value;

    public Value(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return value;
    }
}
