package ru.csc.vindur.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.csc.vindur.Engine;
import ru.csc.vindur.Query;
import ru.csc.vindur.executor.SmartExecutor;
import ru.csc.vindur.storage.*;

import java.util.*;

/**
 * @author Andrey Kokorev
 *         Created on 01.04.2015.
 */
@RestController
public class Controller
{
    private static final String INT_ATTR = "Int";
    private static final String STRING_ATTR = "Str";

    private Engine engine;

    // todo: вынести конфигурацию движка наружу
    // todo: сделать добавление значений
    // todo: обработка разного рода ошибок
    // todo: загрузка данных с диска
    // todo: нужна ли какая-то авторизация?

    public Controller()
    {
        Random r = new Random();
        Map<String, StorageType> indexes = new HashMap<>();
        indexes.put(INT_ATTR, StorageType.RANGE_INTEGER);
        engine = Engine.build()
                .storage(INT_ATTR, new StorageBucketIntegers())
                .storage(STRING_ATTR, new StorageExact<>(String.class))
                .executor(new SmartExecutor(5000))
                .init();

        for(int i = 0; i < 100; i++)
        {
            int doc = engine.createDocument();
            engine.setValue(doc, INT_ATTR, i);
            engine.setValue(doc, STRING_ATTR, (r.nextDouble() < 0.5) ? "Vasia" : "Petya");
        }
    }

    @RequestMapping("/search")
    public ResponseEntity request(@RequestBody Request request)
    {
        Query query = Query.build();
        List<String> storageNotFound = new ArrayList<>();
        request.getQuery().stream()
                .filter((ri) -> engine.getStorages().get(ri.getAttribute()) == null)
                .forEach(ri -> storageNotFound.add(ri.getAttribute()));

        if(storageNotFound.size() > 0)
        {
            StringJoiner storages = new StringJoiner(", ");
            storageNotFound.forEach(storages::add);
            return new ResponseEntity("No storage for [" + storages.toString() + "]", HttpStatus.BAD_REQUEST);
        }

        //At this point we are sure, that exists storage foreach attr in request
        request.getQuery().forEach(ri -> {
            String attr = ri.getAttribute();
            Storage storage = engine.getStorages().get(attr);

            //range request
            if(StorageRangeBase.class.isInstance(storage))
            {
                query.query(attr, StorageRangeBase.range(ri.getFrom(), ri.getTo()));
            }
            else // exact request, "RequestItem.to" ignored
            {
                query.query(attr, ri.getFrom());
            }
        });

        List<ResponseDocument> result = new ArrayList<>();
        engine.executeQuery(query).forEach(id -> result.add(new ResponseDocument(engine.getDocument(id))));

        return new ResponseEntity(result, HttpStatus.OK);
    }

}
