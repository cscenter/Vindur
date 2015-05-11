package ru.csc.vindur.service;

import org.springframework.context.annotation.ImportResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.csc.vindur.Engine;
import ru.csc.vindur.Query;
import ru.csc.vindur.service.model.DocumentModel;
import ru.csc.vindur.service.model.MessageResponce;
import ru.csc.vindur.service.model.InsertionRequest;
import ru.csc.vindur.service.model.Request;
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
    // todo: обработка разного рода ошибок

    public Controller() { }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ResponseEntity request(@RequestBody Request request)
    {
        Query query = Query.build();
        List<String> storageNotFound = new ArrayList<>();
        if(request.getQuery() == null)
        {
            return new ResponseEntity(new MessageResponce("Empty query"), HttpStatus.BAD_REQUEST);
        }

        request.getQuery().stream()
                .filter((ri) -> engine.getStorages().get(ri.getAttribute()) == null)
                .forEach(ri -> storageNotFound.add(ri.getAttribute()));

        if(storageNotFound.size() > 0)
        {
            StringJoiner storages = new StringJoiner(", ");
            storageNotFound.forEach(storages::add);
            return new ResponseEntity(
                    new MessageResponce("No storage for [" + storages.toString() + "]"),
                    HttpStatus.BAD_REQUEST
            );
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


    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public ResponseEntity<MessageResponce> insert(@RequestBody InsertionRequest iRequest)
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
            StringJoiner sj = new StringJoiner("', '");
            storageNotFound.stream().forEach(sj::add);
            return new ResponseEntity<>(
                    new MessageResponce("Storages for ['" + sj.toString() + "'] not found"),
                    HttpStatus.BAD_REQUEST
            );
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

        return new ResponseEntity<>(
                new MessageResponce("Inserted " + iRequest.getDocuments().size() + " document(s)"),
                HttpStatus.OK
        );
    }

    @RequestMapping("/greetings")
    public String greetings()
    {
        return "I am up!";
    }

    // Modify storage
    @RequestMapping(value = "/storage/{attribute}", method = RequestMethod.POST)
    public ResponseEntity<MessageResponce> storage(@PathVariable(value = "attribute") String attr,
                                          @RequestBody Map<String, Object> modification)
    {
        Storage s = engine.getStorages().get(attr);
        if(s == null)
        {
            return new ResponseEntity<>(
                    new MessageResponce(String.format("Storage '%s' does not exist", attr)),
                    HttpStatus.BAD_REQUEST);
        }

        try
        {
            s.modify(modification);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(
                    new MessageResponce(String.format("Modification failed with exception: %s", e.getMessage())),
                    HttpStatus.BAD_REQUEST
            );
        }

        return new ResponseEntity<>(new MessageResponce("Storage modifyed"), HttpStatus.OK);
    }
}
