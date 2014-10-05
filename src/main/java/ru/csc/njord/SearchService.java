package ru.csc.njord;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.csc.njord.entity.Entity;
import ru.csc.njord.entity.EntityDao;
import ru.csc.njord.entity.Value;

import java.util.*;

/**
 * @author: Phillip Delgyado
 * Date: 21.01.14 4:13
 */
@Service
public class SearchService
{
    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

    @Autowired private EntityDao entityDao;

    private Engine engine;
    private BiMap<Integer,String> docs2items;

    private SearchService(EngineConfig config)
    {
        log.debug("Init Engine Config");
        engine = new Engine(config.getExpectedVolume());
        for (String aspect : config.getIndexes().keySet())
            engine.addIndex(aspect, config.getIndexes().get(aspect));
        docs2items = HashBiMap.create();
    }

    public void addItem(String itemId)
    {
        Entity entity = entityDao.findById(itemId);
        addItem(entity);
    }

    public void addItem(Entity entity)
    {
        Integer docId;
        String itemId = entity.getId();

        log.debug("register in search item {}", entity);

        docId = docs2items.inverse().get(itemId);
        if (docId==null)
        {
            docId = engine.registerDocument();
            docs2items.forcePut(docId, itemId);
        }
        else
        {
            engine.clearDocument(docId);
        }

        for (String aspect : entity.getValues().keySet())
            for (Value v : entity.getValues().get(aspect))
               engine.addValue(docId, aspect,v);
    }

    public void removeItem(String itemId)
    {
        Integer docId = docs2items.inverse().get(itemId);
        if (docId==null) return;
        engine.clearDocument(docId);
    }

    public void loadItems()
    {
        log.debug("Load all items to engine");
        Collection<Entity> entities = entityDao.getAllEntities();
        for (Entity entity : entities)
            this.addItem(entity);
    }


    /**
     * @param aspect
     * @return list of items
     */
    public List<Entity> getItemsWithAspect(String aspect)
    {
        log.debug("getAllItemsForAspect with query {}", aspect);
        Collection<Integer> docIds = engine.allItems(aspect);

        List<Entity> entities =new ArrayList<>();
        for (Integer docId : docIds)
        {
            String itemId = docs2items.get(docId);
            if (itemId==null) continue;
            Entity entity = entityDao.findById(itemId); //кэширование на этом уровне!
            if (entity ==null) continue;
            entities.add(entity);
        }
        return entities;

    }


    /**
     * @param query - pair of aspect and value
     * @return list of items
     */
    public List<Entity> getItemsByAspect(String... query)
    {
        log.debug("getItemsByAspectLabels with query {}", Arrays.toString(query));

        Request r = Request.build();
        for (int i=0; i<query.length; )
        {
            String aspect = query[i++];
            String value = query[i++];
            r.exact(aspect,value);
        }

        List<Entity> entities =new ArrayList<>();
        for (Integer docId : engine.findIds(r))
        {
            String itemId = docs2items.get(docId);
            if (itemId==null) continue;
            Entity entity = entityDao.findById(itemId); //кэширование на этом уровне!
            if (entity ==null) continue;
            entities.add(entity);
        }
        return entities;
    }


    /**
     * @param gid - value of GID aspect
     * @return list of items
     */
    public String getIdByGid(String gid)
    {
        log.debug("getIdByGid {}", gid);

        String GID = "blahblah"; //todo: what is Domain.GID ?

        Request r = Request.build();
         r.exact(GID,gid);

        List<Integer> eids = engine.findIds(r);
        if (eids==null) return null;
        if (eids.isEmpty()) return null;
        return docs2items.get(eids.get(0));
    }

}
