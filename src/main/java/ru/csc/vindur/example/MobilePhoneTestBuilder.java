package ru.csc.vindur.example;

import ru.csc.vindur.document.StorageType;
import ru.csc.vindur.document.Value;
import ru.csc.vindur.test.utils.RandomUtils;
import ru.csc.vindur.test.SimpleTest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by Pavel Chursin on 10.11.2014.
 */
public class MobilePhoneTestBuilder {
/*
    private AttributeDescriptor[] attributeDescriptors = new AttributeDescriptor[]{
            cost, manufacturer, model, isSmartphone, screenSize, color, operationSystem, ram
    };*/


    private static Map<String, StorageType> storageTypes;
    private static int lowPriceBound = 3000, highPriceBound = 30000;
    private static String[] manufacturers = {
            "Nokia", "Asus", "Lenovo", "HTC", "Apple", "Huawei", "Sony",
            "Samsung"
    };
    private static double smartphoneProbability = 0.75;
    private static double minScreenSize = 2.0, maxScreenSize = 5.0;
    private static String[] colors = {
            "BLACK", "GRAY", "WHITE", "RED", "GREEN", "BLUE",
            "PINK", "YELLOW", "PURPLE", "ORANGE"
    };
    private static String[] operationSystems = {
            "iOS", "Android", "Windows Phone"
    };
    private int minRAM = 128, maxRAM = 2048;

    private HashSet<Value> uniquePrices, uniqueModels, uniqueRAM, uniqueScreenSize;

    public MobilePhoneTestBuilder() {
        storageTypes = new HashMap<>();
        storageTypes.put("Price", StorageType.NUMERIC);
        storageTypes.put("Manufacturer", StorageType.STRING);
        storageTypes.put("Model", StorageType.STRING);
        storageTypes.put("Smartphone", StorageType.ENUM);
        storageTypes.put("Screen Size", StorageType.STRING);
        storageTypes.put("Color", StorageType.ENUM);
        storageTypes.put("Operation System", StorageType.ENUM);
        storageTypes.put("RAM", StorageType.NUMERIC);

        uniquePrices = new HashSet<>();
        uniqueModels = new HashSet<>();
        uniqueRAM = new HashSet<>();
        uniqueScreenSize = new HashSet<>();
    }


    Map<String,List<Value>> getDocument()
    {
        Value v;
        Map<String,List<Value>> doc = new HashMap<>();

        v = isSmartSupplier();

        doc.put("Smartphone", SimpleTest.list(v));

       //price
        // запоминать сгенерированные значения для запросов в дальнейшем
        v = priceSupplier();
        uniquePrices.add(v);
        doc.put("Price", SimpleTest.list(v));
        //

        v = manufacturerSupplier();
        doc.put("Manufacturer", SimpleTest.list(v));

        v = modelSupplier();
        uniqueModels.add(v);
        doc.put("Model", SimpleTest.list(v));

        v = screenSizeSupplier();
        uniqueScreenSize.add(v);
        doc.put("Screen Size", SimpleTest.list(v));

        v = colorSupplier();
        doc.put("Color", SimpleTest.list(v));

        v = osSupplier();
        doc.put("Operation System", SimpleTest.list(v));

        v = ramSupplier();
        uniqueRAM.add(v);
        doc.put("RAM", SimpleTest.list(v));

       return doc;
    }










    public Map<String, StorageType> getTypes() {
        return storageTypes;
    }

    public Value priceSupplier()
    {
        return new Value(Double.toString(lowPriceBound + Math.random()*(highPriceBound - lowPriceBound)));
    }

    public Value manufacturerSupplier() {
        int index = (int) (Math.random()*manufacturers.length);
        return new Value(manufacturers[index]);
    }

    public Value isSmartSupplier() {
        return new Value(Math.random() < smartphoneProbability ? "Smartphone" : "Phone");
    }

    public Value screenSizeSupplier() {
        double screen = minScreenSize + Math.random()*(maxScreenSize - minScreenSize + 0.1);
        return new Value(String.format("%1.1f", screen));
    }

    public Value modelSupplier() {
        return new Value(RandomUtils.getString(4, 10));
    }

    public Value colorSupplier() {
        int index = (int) (Math.random()*colors.length);
        return new Value(colors[index]);
    }

    public Value osSupplier() {
        int index = (int) (Math.random()*operationSystems.length);
        return new Value(operationSystems[index]);
    }

    public Value ramSupplier() {
        int ram = minRAM + (int) (Math.random()*(maxRAM - minRAM + 1));
        return new Value(Integer.toString(ram));
    }

}
