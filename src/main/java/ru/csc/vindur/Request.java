package ru.csc.vindur;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/**
 * @author: Phillip Delgyado
 * Date: 30.10.13 23:36
 * Поисковый запрос
 */
public class Request
{
    private Map<String,RequestPart> reqs; //aspect->RequestPart

    private Request()
    {
        reqs = new HashMap<>();
    }

    public static Request build()
    {
        Request r = new Request();
        return r;
    }

    public Request exact(String tag, String value)
    {
        RequestPart rp = new RequestPart();
        rp.tag=tag;
        rp.from=value;
        rp.to=value;
        rp.isExact=true;
        reqs.put(tag, rp);
        return this;
    }

    public Request range(String tag, String from, String to)
    {
        RequestPart rp = new RequestPart();
        rp.tag=tag;
        rp.from=from;
        rp.to=to;
        rp.isExact=false;
        reqs.put(tag, rp);
        return this;
    }

    public Collection<RequestPart> getRequestParts()
    {
        return Collections.unmodifiableCollection(reqs.values());
    }

    public RequestPart get(String aspect)
    {
        return reqs.get(aspect);
    }

    public static class RequestPart
    {
        String tag;
        String from;
        String to;
        boolean isExact;

        @Override
        public String toString()
        {
            return "search in " + "'" + tag + '\'' +
                    " for ('" + from + '\'' +
                    ", '" + to + '\'' +
                    ')';
        }
    }

    @Override
    public String toString()
    {
        return "Request{" + reqs.values() +  '}';
    }
}