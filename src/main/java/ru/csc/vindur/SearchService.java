package ru.csc.vindur;

import ru.csc.vindur.entity.Entity;
import ru.csc.vindur.entity.EntityDao;
import ru.csc.vindur.entity.Value;

import java.util.ArrayList;

/**
 * Created by Pavel Chursin on 05.10.2014.
 */
public class SearchService {
    Engine engine;
    EntityDao entityDao;

    public SearchService(EngineConfig engineConfig) {
        int maxsize = engineConfig.getExpectedVolume();
        engine = new Engine(maxsize);
        entityDao = new EntityDao(engineConfig.getFileName());

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
    }


}
