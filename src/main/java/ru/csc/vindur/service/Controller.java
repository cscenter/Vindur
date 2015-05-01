package ru.csc.vindur.service;

import org.springframework.context.annotation.ImportResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.csc.vindur.Engine;
import ru.csc.vindur.Query;
import ru.csc.vindur.storage.*;

import java.util.*;

/**
 * @author Andrey Kokorev
 *         Created on 01.04.2015.
 */
@ImportResource("engineConfiguration.xml")
@RestController
public class Controller
{
    private Engine engine;

    public void setEngine(Engine engine)
    {
        this.engine = engine;
    }
    // todo: вынести конфигурацию движка наружу
    // todo: сделать добавление значений
    // todo: обработка разного рода ошибок
    // todo: загрузка данных с диска
    // todo: нужна ли какая-то авторизация?
    // todo: может быть опциональный декодер строка -> объект для полученных/передаваемых JSON запросов?

    public Controller()
    {
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

        //At this point we are sure, that proper storage exists for each attr in request
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

        List<DocumentModel> result = new ArrayList<>();
        engine.executeQuery(query).forEach(id -> result.add(new DocumentModel(engine.getDocument(id))));

        return new ResponseEntity(result, HttpStatus.OK);
    }


    @RequestMapping("/insert")
    public ResponseEntity insert(@RequestBody InsertionRequest iRequest)
    {
        Set<String> storageNotFound = new HashSet<>();
        Map<String, Storage> storages = engine.getStorages();

        //todo: Может нужно возвращать, каких конкретно хранилищ нет?
        //Check if any of documents can not be inserted
        iRequest.getDocuments().stream().forEach(doc ->
            doc.getValues().keySet().stream()
                    .filter(attr -> !storages.containsKey(attr))
                    .forEach(storageNotFound::add)
        );

        if(storageNotFound.size() > 0)
        {
            StringJoiner sj = new StringJoiner(", ");
            storageNotFound.stream().forEach(sj::add);
            return new ResponseEntity("No proper storages for attributes [" + sj.toString() + "] ", HttpStatus.BAD_REQUEST);
        }

        //todo: Проверка типов какая-то?
        iRequest.getDocuments().stream().forEach(doc -> {
            int id = engine.createDocument();
            doc.getValues().entrySet().stream()
                    .forEach(entry -> {
                        for(Object value : entry.getValue())
                            engine.setValue(id, entry.getKey(), value);
                    });
        });

        return new ResponseEntity("Inserted " + iRequest.getDocuments().size() + " documents", HttpStatus.OK);
    }

    @RequestMapping("/greetings")
    public String greetings()
    {
        return "I am up!";
    }
}
