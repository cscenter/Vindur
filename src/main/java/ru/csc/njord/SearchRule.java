package ru.csc.njord;

/**
* @author: Phillip Delgyado
* Date: 07.03.14 5:40
*/
public class SearchRule
{
    String aspect;
    Long complexity;
    IIndex ai;
    Request.RequestPart rp;

    public SearchRule(String aspect, Long complexity, IIndex ai, Request.RequestPart rp)
    {
        this.aspect = aspect;
        this.complexity = complexity;
        this.ai=ai;
        this.rp=rp;
    }
}
