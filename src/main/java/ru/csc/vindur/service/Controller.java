package ru.csc.vindur.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.csc.vindur.Engine;
import ru.csc.vindur.Query;
import ru.csc.vindur.executor.SmartExecutor;
import ru.csc.vindur.storage.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Andrey Kokorev
 *         Created on 01.04.2015.
 */
@RestController
public class Controller
{
    private static final String INT_ATTR = "Int";
    private Engine engine;

    // todo: вынести конфигурацию движка наружу
    // todo: сделать добавление значений
    // todo: страница ошибки
    // todo: нужна ли какая-то авторизация?

    public Controller()
    {
        Map<String, StorageType> indexes = new HashMap<>();
        indexes.put(INT_ATTR, StorageType.RANGE_INTEGER);
        engine = Engine.build()
                .storage(INT_ATTR, new StorageBucketIntegers())
                .executor(new SmartExecutor(5000))
                .init();

        for(int i = 0; i < 100; i++)
        {
            int doc = engine.createDocument();
            engine.setValue(doc, INT_ATTR, i);
            engine.setValue(doc, INT_ATTR, 10 * i);
        }
    }

    @RequestMapping("/greetings")
    public Response response(@RequestParam(value="name", defaultValue="World") String name)
    {
        return new Response("Hello, " + name + "!");
    }

    @RequestMapping("/search")
    public Response response(@RequestParam(value="attribute", defaultValue = INT_ATTR) String attr,
                             @RequestParam(value="from", defaultValue = "1")      String f,
                             @RequestParam(value="to",   defaultValue = "100")    String t)
    {
        int from = Integer.parseInt(f);
        int to = Integer.parseInt(t);
        Query query = Query.build().query(attr, StorageBucketIntegers.range(from, to));

        List<Integer> result = engine.executeQuery(query);
        StringBuilder str = new StringBuilder("Result: ");
        for(Integer id : result)
        {
            str.append(getStringRepresentation(engine.getDocument(id).getValues(attr)));
            str.append(", ");
        }
        return new Response(str.toString());
    }

    private String getStringRepresentation(List<Object> object)
    {
        StringBuilder sb = new StringBuilder();
        List<String> strValues = new ArrayList<>();
        object.forEach((v) -> strValues.add(v.toString()));

        boolean first = true;
        sb.append('{');
        for(String s : strValues)
        {
            if(first) first = false;
            else sb.append(", ");
            sb.append(s);
        }
        sb.append('}');
        return sb.toString();
    }
}
