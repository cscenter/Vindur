package ru.csc.vindur;

import ru.csc.vindur.Request;

import java.util.List;

/**
 * Created by Edgar Zhavoronkov on 13.10.14.
 */
public class RequestPlan
{
    private List<PlanElement> planElements; // aspect => value
    private Request request;

    public RequestPlan(Request request)
    {
        this.request = request;
        for (Request.RequestPart requestPart : request.getRequestParts())
            planElements.add(new PlanElement(requestPart));
    }

    public List<PlanElement> getPlanElements()
    {
        return planElements;
    }

    public static class PlanElement
    {
        Request.RequestPart requestPart;

        public PlanElement(Request.RequestPart requestPart)
        {
            this.requestPart = requestPart;
        }

        public Request.RequestPart getRequestPart()
        {
            return requestPart;
        }


    }

}
