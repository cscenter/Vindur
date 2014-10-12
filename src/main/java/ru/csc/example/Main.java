package ru.csc.example;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ru.csc.vindur.*;
import ru.csc.vindur.entity.Entity;
import ru.csc.vindur.entity.Value;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author Andrey Kokorev
 * Created on 25.09.2014.
 */
public class Main {
    SearchService searchService;

    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {

        //Generation
        String[] aspects = new String[] {
                "name", "gid", "specCode",
                "creatorUserId", "createDate",
                "lastEditDate", "price"
        };
        int num = 1000;
        /*
        Generate something using generate enum, then chose some values and execute request with that values
        try {
            RandomDataGenerator gen = new RandomDataGenerator(new FileOutputStream(new File("data.txt")));
            gen.generateEnum(aspects, 3, num, 3);
            gen.close();
        }catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        */

        //Configuration
        HashMap<String, IndexDefinition> indexes = new HashMap<>();
        for(String aspect : aspects) {
            indexes.put(aspect, new IndexDefinition(ValueType.STRING));
        }

        EngineConfig config = new EngineConfig(indexes, num, "data.txt");
        searchService = new SearchService(config);

        //Load everything
        searchService.loadEntities();

        // Values from file generated locally
        Request p = Request.build().exact("name", "qR").exact("specCode", "6M");

        for(Entity entity : searchService.search(p)) {
            System.out.println(entity);
        }
    }
}
