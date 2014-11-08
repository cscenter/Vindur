package ru.csc.vindur;

import com.google.common.base.Supplier;

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
    private final Map<String,RequestPart> reqs = new HashMap<>(); //aspect->RequestPart

    public static Request build()
    {
        return new Request();
    }

    public Request exact(String tag, String value)
    {
        RequestPart requestPart = new RequestPart();
        requestPart.tag=tag;
        requestPart.from=value;
        requestPart.to=value;
        requestPart.isExact=true;
        reqs.put(tag, requestPart);
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
        private String tag;
        private String from;
        private String to;
        private boolean isExact;

        public boolean isExact() {
            return isExact;
        }

        public String getTag() {
            return tag;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }

        @Override
        public String toString()
        {
        	if(isExact) {
        		return String.format("search in '%s' for exact '%s'", tag, from);
        	} else {
        		return String.format("search in '%s' for range ['%s', '%s']", tag, from, to);
        	}
        }
    }

    @Override
    public String toString()
    {
        return "Request{" + reqs.values() +  '}';
    }
}