package ru.csc.vindur.example;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import ru.csc.vindur.Query;
import ru.csc.vindur.storage.StorageType;
import ru.csc.vindur.test.utils.RandomUtils;

/**
 * Created by Pavel Chursin on 10.11.2014.
 */
public class MobilePhoneTestBuilder {
    /*
     * private AttributeDescriptor[] attributeDescriptors = new
     * AttributeDescriptor[]{ cost, manufacturer, model, isSmartphone,
     * screenSize, color, operationSystem, ram };
     */

    private static Map<String, StorageType> storageTypes;
    private static int lowPriceBound = 3000, highPriceBound = 30000;
    private static String[] manufacturers = { "Nokia", "Asus", "Lenovo", "HTC",
            "Apple", "Huawei", "Sony", "Samsung" };
    private static double smartphoneProbability = 0.75;
    private static double minScreenSize = 2.0, maxScreenSize = 5.0;
    private static String[] colors = { "BLACK", "GRAY", "WHITE", "RED",
            "GREEN", "BLUE", "PINK", "YELLOW", "PURPLE", "ORANGE" };
    private static String[] operationSystems = { "iOS", "Android",
            "Windows Phone" };
    private int minRAM = 128, maxRAM = 2048;

    private int vetoAttributes = 1;

    private HashSet<Object> uniquePrices, uniqueModels, uniqueRAM,
            uniqueScreenSize;

    public MobilePhoneTestBuilder() {
        storageTypes = new HashMap<>();
        storageTypes.put("Price", StorageType.STRING);
        storageTypes.put("Manufacturer", StorageType.STRING);
        storageTypes.put("Model", StorageType.STRING);
        storageTypes.put("Smartphone", StorageType.STRING);
        storageTypes.put("Screen Size", StorageType.STRING);
        storageTypes.put("Color", StorageType.STRING);
        storageTypes.put("Operation System", StorageType.STRING);
        storageTypes.put("RAM", StorageType.STRING);

        uniquePrices = new HashSet<>();
        uniqueModels = new HashSet<>();
        uniqueRAM = new HashSet<>();
        uniqueScreenSize = new HashSet<>();
    }

    Map<String, List<Object>> getDocument() {
        Object v;
        Map<String, List<Object>> doc = new HashMap<>();

        v = isSmartSupplier();

        doc.put("Smartphone", Arrays.asList(v));

        // price
        // запоминать сгенерированные значения для запросов в дальнейшем
        v = priceSupplier();
        uniquePrices.add(v);
        doc.put("Price", Arrays.asList(v));
        //

        v = manufacturerSupplier();
        doc.put("Manufacturer", Arrays.asList(v));

        v = modelSupplier();
        uniqueModels.add(v);
        doc.put("Model", Arrays.asList(v));

        v = screenSizeSupplier();
        uniqueScreenSize.add(v);
        doc.put("Screen Size", Arrays.asList(v));

        v = colorSupplier();
        doc.put("Color", Arrays.asList(v));

        v = osSupplier();
        doc.put("Operation System", Arrays.asList(v));

        v = ramSupplier();
        uniqueRAM.add(v);
        doc.put("RAM", Arrays.asList(v));

        return doc;
    }

    public Query getRandomAttributesRequest() {
        int attributes = (int) (Math.random() * (storageTypes.size() - vetoAttributes)) + 1;
        return getMultiAttributesRequest(attributes);
    }

    public Query getMultiAttributesRequest(int n) {
        if (n > storageTypes.size() - vetoAttributes) {
            throw new IllegalArgumentException();
        }

        HashMap<String, Object> randomAttributes = new HashMap<>();
        Object v;

        v = isSmartSupplier();
        randomAttributes.put("Smartphone", v);

        do {
            v = priceSupplier();
        } while (!uniquePrices.contains(v));
        randomAttributes.put("Price", v);

        v = manufacturerSupplier();
        randomAttributes.put("Manufacturer", v);

        // я решил пока что не делать запросы по моделям, потому что таким
        // образом
        // их можно очень долго генерить до тех пор, пока мы не найдем
        // существующую
        // do {
        // v = modelSupplier();
        // } while (!uniqueModels.contains(v));
        // randomAttributes.put("Model", v);

        do {
            v = screenSizeSupplier();
        } while (!uniqueScreenSize.contains(v));
        randomAttributes.put("Screen Size", v);

        v = colorSupplier();
        randomAttributes.put("Color", v);

        v = osSupplier();
        randomAttributes.put("Operation System", v);

        do {
            v = ramSupplier();
        } while (!uniqueRAM.contains(v));
        randomAttributes.put("RAM", v);

        String[] types = (String[]) randomAttributes.keySet().toArray();
        Query query = Query.build();

        for (int i = 0; i < n; i++) {
            String type;
            do {
                int randPos = (int) (Math.random() * types.length);
                type = types[randPos];
            } while (!randomAttributes.containsKey(type));
            query.query(type, randomAttributes.get(type));
            randomAttributes.remove(type);
        }

        return query;
    }

    public Map<String, StorageType> getTypes() {
        return storageTypes;
    }

    public Object priceSupplier() {
        return Double.toString(lowPriceBound + Math.random()
                * (highPriceBound - lowPriceBound));
    }

    public Object manufacturerSupplier() {
        int index = (int) (Math.random() * manufacturers.length);
        return manufacturers[index];
    }

    public Object isSmartSupplier() {
        return Math.random() < smartphoneProbability ? "Smartphone" : "Phone";
    }

    public Object screenSizeSupplier() {
        double screen = minScreenSize + Math.random()
                * (maxScreenSize - minScreenSize + 0.1);
        return String.format("%1.1f", screen);
    }

    public Object modelSupplier() {
        return RandomUtils.getString(4, 10);
    }

    public Object colorSupplier() {
        int index = (int) (Math.random() * colors.length);
        return colors[index];
    }

    public Object osSupplier() {
        int index = (int) (Math.random() * operationSystems.length);
        return operationSystems[index];
    }

    public Object ramSupplier() {
        int ram = minRAM + (int) (Math.random() * (maxRAM - minRAM + 1));
        return Integer.toString(ram);
    }

}
