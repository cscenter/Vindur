package ru.csc.njord;

import com.beust.jcommander.internal.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.csc.njord.entity.Value;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Phillip Delgyado
 * Date: 30.10.13 15:31
 *
 * Движок для поиска по атрибутам
 */
public class Engine
{
    private static final Logger log = LoggerFactory.getLogger(Engine.class);

    /* TODO dph
       4) Доделать проверки при фильтрации - по всем невошедшим полям (? а надо)
       4.1) Вспомнить, где надо, про физику (вернее, понимаем, какие методы нужны от нее)
       5) Делаем выборку по диапазону (для подходящих storage'й (1 day)
       6) Делаем сервисную обвязку с многопоточностью (
       ограниченное количество поисковых запросов,
       добавление пачкой при stopTheWorld
       хранение результатов с последовательной загрузкой(1 day)
       7) Реализуем удаление элементов (дорого очень, так что просто список удаленных в специальном сторадже) 1 day
       8) Сделать условие (среди нескольких значений аспекта)
     */

    private AtomicInteger            itemCounter;
    private Map<String, StorageType> types;
    private Map<String, IIndex>      indexes;
    private Map<Integer,Bulk>        documents;

    private int maxsize;

    public Engine(int size)
    {
        types = new HashMap<>();
        indexes = new HashMap<>();
        itemCounter = new AtomicInteger(0);
        documents = new HashMap<>();
        maxsize = size;
    }

    public void addIndex(String aspect, EngineConfig.IndexDefinition def)
    {
        log.debug("register index for aspect {} with type {}", aspect,def.getType());
        types.put(aspect, def.getType());
        if (def.getType() == StorageType.values)
            indexes.put(aspect,new IndexStrings(maxsize));
        if (def.getType() == StorageType.numerics)
            indexes.put(aspect,new IndexNumerics(maxsize).setConverter(def.getNumberConverter()));
        if (def.getType() == StorageType.enums)
            indexes.put(aspect,new IndexEnums(maxsize));
    }

   public int registerDocument()
   {
      Integer docId = itemCounter.incrementAndGet();
      documents.put(docId,new Bulk(docId));
      return docId;
   }

   public void addValue(int docId, String aspect, Value value)
   {
      if (!documents.containsKey(docId))
      {
         log.debug("Document {} not registered");
         return; //а вот нет такого документа
      }
      documents.get(docId).registerValue(docId,aspect,value);

      if (!indexes.containsKey(aspect))
      {
         log.debug("can't find index for aspect: {}",aspect);
         addIndex(aspect,EngineConfig.IndexDefinition.DefIndexDefinition()); //todo убрать физику
      }
      indexes.get(aspect).add(docId,value);

   }

    public void clearDocument(Integer docId)
    {
        Bulk h = documents.get(docId);
        if (h==null) return;
        for (String aspect : h.values.keySet())
         {
             if (!indexes.containsKey(aspect)) return;
             for (Value v : h.values.get(aspect))
                 indexes.get(aspect).remove(docId,v);
         }
         h.values.clear();
    }

    public long getCount(String aspect)
    {
        return indexes.get(aspect).size();
    }

   /* ****************************************************************************************** */
   public Collection<Integer> allItems(String aspect)
   {
       IIndex idx = indexes.get(aspect);
       if (idx==null) return Lists.newArrayList();
       return idx.getAll();
   }



  public List<Integer> findIds(Request request)
  {
      log.debug("find {}",request);
      List<SearchRule> lc = new ArrayList<>();

      // Для всех элементов реквеста определить сложность
      for (Request.RequestPart rp : request.getRequests())
      {
         String aspect = rp.tag;
         IIndex ai = indexes.get(aspect);
         if (ai==null) continue; //ну, ошиблись и нет такого аспекта
         SearchRule c = new SearchRule(aspect, ai.expectAmount(rp.to),ai,rp); //для диапазона - думаем когда-нибудь
         lc.add(c);
      }

      Collections.sort(lc, new Comparator<SearchRule>() {
          @Override
          public int compare(SearchRule o1, SearchRule o2) {
              return -o1.complexity.compareTo(o2.complexity);
          }
      });

      BitSet r = null;
      for (SearchRule c : lc)
      {
        if (r==null)
            r = c.ai.findSet(c.rp.to);
        else
            r.and(c.ai.findSet(c.rp.to));
      }

     //а теперь проверяем руками результат
      List<Integer> result = new ArrayList<>();
      if (r==null) return result;
      for (int docId = r.nextSetBit(0); docId >= 0; docId = r.nextSetBit(docId+1))
      {
          Bulk h = documents.get(docId);
          //checkRule(lc.get(0).rp,i); //todo dph
          result.add(docId);
      }
     return result;
  }

  private boolean checkRule(Request.RequestPart rp, Bulk i)
  {
      String aspect = rp.tag;
      List<Value> vals = i.values.get(aspect);
      if (rp.isExact)
      {
          for (Value v : vals)
              if (v.getValue().equals(rp.to)) return true;
      }
      return false;
  }

  public Collection<Integer> findExactString(String tag, String match)
    {
       Request r = Request.build().exact(tag,match);
       return findIds(r);
    }

   /* ***************************** test methods ************************************/
   public Set<String> getIndexes()
   {
       return types.keySet();
   }

   public StorageType getIndexType(String aspect)
   {
       return types.get(aspect);
   }

}
