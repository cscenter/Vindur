package ru.csc.vindur.example.sample_db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import ru.csc.vindur.Engine;
import ru.csc.vindur.Query;
import ru.csc.vindur.bitset.BitArray;
import ru.csc.vindur.bitset.EWAHBitArray;
import ru.csc.vindur.executor.TinyExecutor;
import ru.csc.vindur.storage.*;

/**
 * @author Andrey Kokorev Created on 28.11.2014.
 */
public class MarketExample {
    public static void main(String[] args) throws IOException
    {
        Supplier<BitArray> bs =  EWAHBitArray::new;

        Engine engine =
              Engine.build()
                  .executor(new TinyExecutor())
                  .storage("title", new StorageExact<>(String.class))
                  .storage("brand", new StorageExact<>(String.class))
                  .storage("model", new StorageExact<>(String.class))
                  .storage("categoryName", new StorageExact<>(String.class))
                  .storage("priceLow", new StorageBucketIntegers())
                  .storage("priceHigh", new StorageBucketIntegers())
                  .storage("description", new StorageLucene())
                .init();

        Map<Integer, Map<String, List<Object>>> docs = new HashMap<>();

        Path data = Paths.get("example_data");
        Files.list(data).forEach((x) -> {
            System.out.println("parsing " + x.toString());
            XMLDataFileParser parser = new XMLDataFileParser(x.toString());
            List<Map<String, List<Object>>> entities = parser.getEntities();

            for (Map<String, List<Object>> entity : entities) {
                int docId = engine.createDocument();
                docs.put(docId, entity);

                for (String attr : entity.keySet()) {
                    for (Object value : entity.get(attr)) {
                        engine.setAttributeByDocId(docId, attr, value);
                    }
                }
            }
        });

        System.out.println("\n\n=================Bikes");
        Query bikes = Query.build().query("categoryName", "Велосипеды");

        List<Integer> bikeResult = engine.executeQuery(bikes);

        for (Integer docId : bikeResult) {
            Map<String, List<Object>> bike = docs.get(docId);
            if (bike == null) {
                System.out.println("ERROR!");
                continue;
            } else {
                printDocument(bike);
            }
        }

        System.out.println("\n\n=================Memory cards");
        Query memoryCards = Query.build().query("categoryName", "Карты памяти");

        List<Integer> mcResult = engine.executeQuery(memoryCards);

        for (Integer docId : mcResult) {
            Map<String, List<Object>> card = docs.get(docId);
            if (card == null) {
                System.out.println("ERROR!");
                continue;
            } else {
                printDocument(card);
            }
        }

        System.out
                .println("\n\n=================Everything from 5000 to 10000");
        Query cheap = Query
                .build()
                .query("priceHigh", StorageRangeBase.generateRequest(0, 10000))
                .query("priceLow",
                        StorageRangeBase.generateRequest(5000,
                                Integer.MAX_VALUE));

        List<Integer> cheapResult = engine.executeQuery(cheap);

        for (Integer docId : cheapResult) {
            Map<String, List<Object>> stuff = docs.get(docId);
            if (stuff == null) {
                System.out.println("ERROR!");
                continue;
            } else {
                printDocument(stuff);
            }
        }
    }

    private static void printDocument(Map<String, List<Object>> doc) {
        System.out.println("-----------------------------------");
        System.out.println("Title = " + doc.get("title").get(0));
        System.out.println("Category = " + doc.get("categoryName").get(0));
        System.out.println("Lowest price = " + doc.get("priceLow").get(0));
        System.out.println("Highest price = " + doc.get("priceHigh").get(0));
        for (String attr : doc.keySet()) {
            if (attr.equals("title") || attr.equals("priceLow")
                    || attr.equals("priceHigh") || attr.equals("categoryName")) {
                continue;
            }
            System.out.print(attr + " ");
            System.out.println(Arrays.toString(doc.get(attr).toArray()));
        }
    }
}
