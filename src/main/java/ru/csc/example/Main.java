package ru.csc.example;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ru.csc.vindur.SearchService;
import ru.csc.vindur.entity.Entity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
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
        String[] aspects = new String[] {
                "name", "gid", "specCode",
                "creatorUserId", "createDate",
                "lastEditDate", "price"
        };
        int num = 1000;

        try {
            RandomDataGenerator gen = new RandomDataGenerator(new FileOutputStream(new File("data.txt")));
            gen.generate(aspects, 3, num);
            gen.close();
        }catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }
}
