package ru.csc.njord.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StreamTokenizer;
import java.util.*;

/**
 * @author Andrey Kokorev
 * Created on 25.09.2014.
 */
@Service
public class EntityDao {
    HashMap<String, Entity> storage;  // id -> Entity

    public static final Logger log = LoggerFactory.getLogger(EntityDao.class);

    public EntityDao(String fileName) {
        loadFromFile(fileName);
    }

    public EntityDao(HashMap<String, Entity> storage) {
        this.storage = storage;
    }

    private void loadFromFile(String filename) {
        try {
            Scanner s = new Scanner(new FileInputStream(new File(filename)));
            int aspectNum = s.nextInt();
            int count = s.nextInt();

            String[] aspects = new String[aspectNum];
            for(int i = 0; i < aspectNum; i++)
                aspects[i] = s.next();

            storage = new HashMap<>(count);
            for(int c = 0; c < count; c++) {
                Entity e = new Entity(Integer.toString(c));
                Map<String, ArrayList<Value>> vals = e.getValues();
                for(int i = 0; i < aspectNum; i++) {
                    ArrayList<Value> av = new ArrayList<>(1);
                    av.add(new Value(s.next()));

                    vals.put(aspects[i], av);
                }
                storage.put(Integer.toString(c), e);
            }

            s.close();
        }catch (Exception e) {
            log.error(e.getMessage());
            log.error(Arrays.deepToString(e.getStackTrace()));
        }
    }

    public Entity findById(String id) {
        return storage.get(id); //TODO: logic
    }

    public Collection<Entity> getAllEntities() {
        return storage.values();
    }
}
