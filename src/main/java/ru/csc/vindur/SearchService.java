package ru.csc.vindur;

import ru.csc.vindur.entity.Entity;
import ru.csc.vindur.entity.EntityDao;
import ru.csc.vindur.entity.Value;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Pavel Chursin on 05.10.2014.
 */
public class SearchService {
    Engine engine;
    EntityDao entityDao;
    HashMap<Integer, String> docs2entities;

    public SearchService(EngineConfig engineConfig) {
        int maxsize = engineConfig.getExpectedVolume();
        engine = new Engine(maxsize);
        entityDao = new EntityDao(engineConfig.getFileName());
        docs2entities = new HashMap<>(engineConfig.expectedVolume);

        createIndex(engineConfig, maxsize);
    }

    private void createIndex(EngineConfig engineConfig, int maxsize) {
        for (String attribute : engineConfig.getIndexes().keySet()) {
            IndexDefinition definition = engineConfig.getIndexes().get(attribute);
            switch(definition.getValueType()) {
                case NUMERIC:
                    IndexNumerics indexNumerics = new IndexNumerics(maxsize);
                    indexNumerics.setConverter(definition.getConverter());
                    engine.addIndex(attribute, indexNumerics);
                    break;
                case ENUM:
                    engine.addIndex(attribute, new IndexEnums(maxsize));
                    break;
                case STRING:
                default:
                    engine.addIndex(attribute, new IndexStrings(maxsize));
                    break;
            }
        }
    }

    public void loadEntities() {
        for (Entity entity : entityDao.getAllEntities()) {
            addEntity(entity);
        }
    }

    private void addEntity(Entity entity) {
        int docId = engine.createDocument();
        for (String attribute : entity.getValues().keySet()) {
            ArrayList<Value> values = entity.getValues().get(attribute);
            engine.addValuesListByDocId(docId, attribute, values);
        }
        docs2entities.put(docId, entity.getId());
    }

    public ArrayList<Entity> search(Request request) {
        ArrayList<Integer> resultDocs = engine.executeRequest(request);
        ArrayList<Entity> result = new ArrayList<>(resultDocs.size());
        for(Integer i : resultDocs) {
            result.add(entityDao.findById(docs2entities.get(i)));
        }
        return result;
    }
}
