package ru.csc.vindur.service.model;

/**
 * @author Andrey Kokorev
 *         Created on 11.05.2015.
 */
public class MessageResponce
{
    private String message;
    public MessageResponce(String error)
    {
        this.message = error;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
