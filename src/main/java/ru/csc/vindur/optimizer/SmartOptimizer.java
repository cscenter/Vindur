package ru.csc.vindur.optimizer;

import ru.csc.vindur.Engine;
import ru.csc.vindur.Request;

/**
 * Created by Edgar_Work on 11.11.2014.
 */
public class SmartOptimizer implements Optimizer
{
    //todo: здесь уже придется знать список частей запроса и размер текущего результтата?
    @Override
    public Plan generatePlan(Request request, Engine engine)
    {
        return null;
    }

    @Override
    public void updatePlan(Plan plan, int currentResultSize)
    {
        // todo: здесь как-то надо понять, что результат небеольшой, остановиться и проверять вручную? Или проапдейтить размер результата?

    }
}
