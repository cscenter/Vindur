package ru.csc.vindur.service;

/**
 * @author Andrey Kokorev
 *         Created on 01.04.2015.
 */
public class Response
{
    private final String text;

    public Response(String text)
    {
        this.text = text;
    }

    public String getText()
    {
        return text;
    }
}
