package ru.csc.vindur.example;

/**
 * Created by Pavel Chursin on 29.10.2014.
 */
public class MobilePhoneTest {

    // private final EngineConfig simpleEngineConfig;
    // private final int documentsCount;
    // private final int requestsCount;
    // private final int reqAttributesCount;
    // private int currentArt;
    //
    // private static final double phoneChance = 0.25;
    // private static final int[] costBounds = {3000, 5000, 7500, 10000, 15000,
    // 25000, 200000};
    // private AttributeDescriptor[] attributeDescriptors = new
    // AttributeDescriptor[]{
    // cost, manufacturer, model, isSmartphone, screenSize, color,
    // operationSystem, ram
    // };
    //
    // private static final Manufacturer[][] smartManufacturers = new
    // Manufacturer[][]{
    // //1st cat: [3000, 5000]
    // {
    // new Manufacturer("Nokia", 0.95),
    // new Manufacturer("Asus", 0.96), new Manufacturer("Huawei", 0.98),
    // new Manufacturer("Lenovo", 0.99), new Manufacturer("ZTE", 1.0)
    // },
    // //2: [5000, 7500]
    // {
    // new Manufacturer("Samsung", 0.48), new Manufacturer("LG", 0.53),
    // new Manufacturer("Sony", 0.54), new Manufacturer("Nokia", 0.98),
    // new Manufacturer("Huawei", 0.985), new Manufacturer("Asus", 0.99),
    // new Manufacturer("Lenovo", 0.995), new Manufacturer("ZTE", 1.0)
    // },
    // //3: [7500, 10000]
    // {
    // new Manufacturer("HTC", 0.2), new Manufacturer("Samsung", 0.43),
    // new Manufacturer("Sony", 0.54), new Manufacturer("Asus", 0.55),
    // new Manufacturer("Nokia", 0.97), new Manufacturer("Huawei", 0.98),
    // new Manufacturer("Lenovo", 0.99), new Manufacturer("ZTE", 1.0)
    // },
    // //4: [10000, 15000]
    // {
    // new Manufacturer("HTC", 0.08),
    // new Manufacturer("Samsung", 0.56), new Manufacturer("LG", 0.59),
    // new Manufacturer("Sony", 0.63), new Manufacturer("Nokia", 0.98),
    // new Manufacturer("Asus", 0.985), new Manufacturer("Huawei", 0.99),
    // new Manufacturer("Lenovo", 0.995), new Manufacturer("ZTE", 1.0)
    // },
    // //5: [15000, 25000]
    // {
    // new Manufacturer("Apple", 0.08), new Manufacturer("HTC", 0.42),
    // new Manufacturer("Samsung", 0.54), new Manufacturer("LG", 0.56),
    // new Manufacturer("Sony", 0.71), new Manufacturer("Nokia", 1.0)
    // },
    // //6: [20000, ...(100000)]
    // {
    // new Manufacturer("Apple", 0.66), new Manufacturer("HTC", 0.74),
    // new Manufacturer("Samsung", 0.99), new Manufacturer("Nokia", 1.0)
    // }
    //
    // };
    // private static final Manufacturer[][] phoneManufacturers = new
    // Manufacturer[][]{
    // //1st cat: [3000, 5000]
    // {
    // new Manufacturer("Alcatel", 0.09), new Manufacturer("Explay", 0.17),
    // new Manufacturer("Fly", 0.28), new Manufacturer("Nokia", 0.5),
    // new Manufacturer("LG", 0.53), new Manufacturer("Philips", 0.57),
    // new Manufacturer("Samsung", 0.67), new Manufacturer("BQ", 0.74),
    // new Manufacturer("KENEKSI", 0.79), new Manufacturer("MAXVI", 0.85),
    // new Manufacturer("Sony Ericsson", 0.94), new Manufacturer("teXet", 1.0)
    // },
    // //2d cat
    // {
    // new Manufacturer("Explay", 0.02), new Manufacturer("Nokia", 0.48),
    // new Manufacturer("Philips", 0.61), new Manufacturer("Samsung", 0.91),
    // new Manufacturer("Sony Ericsson", 0.98), new Manufacturer("teXet", 1.0)
    // },
    // //3
    // {
    // new Manufacturer("Nokia", 0.61), new Manufacturer("Philips", 0.67),
    // new Manufacturer("Samsung", 0.83), new Manufacturer("LG", 1.0)
    // },
    // //4
    // {
    // new Manufacturer("Nokia", 0.75),
    // new Manufacturer("Samsung", 0.87), new Manufacturer("LG", 1.0)
    // },
    // //5
    // {
    // new Manufacturer("Nokia", 1.0)
    // },
    // //6
    // {
    // new Manufacturer("Nokia", 0.6), new Manufacturer("Vertu", 1.0)
    // },
    // };
    //
    // private static final AttributeDescriptor isSmartphone = new
    // AttributeDescriptor("Is smartphone", StorageType.ENUM) {
    // @Override
    // public Value generateValue(Object... params) {
    // double chance = Math.random();
    // return new Value((chance > phoneChance) ? "true" : "false");
    // }
    // };
    //
    // // takes 2 parameters: boolean isSmart, int category
    // private static final AttributeDescriptor manufacturer = new
    // AttributeDescriptor("Manufacturer", StorageType.ENUM) {
    // @Override
    // public Value generateValue(Object... params) {
    // boolean smart = (boolean) params[0];
    // int category = (int) params[1];
    // Manufacturer[] targetManufactorer = (smart ? smartManufacturers[category]
    // : phoneManufacturers[category]);
    // double chance = Math.random();
    // int hit = Arrays.binarySearch(targetManufactorer, chance);
    // if (hit < 0) {
    // hit = - hit - 1;
    // }
    // return new Value(targetManufactorer[hit].getName());
    // }
    // };
    //
    // private static final AttributeDescriptor color = new
    // AttributeDescriptor("Color", StorageType.ENUM) {
    // private final MainColor[] colors = MainColor.values();
    // private double[] freqs;
    //
    // {
    // freqs = new double[colors.length];
    // for (int i = 0; i < colors.length; i++) {
    // freqs[i] = colors[i].getFrequency();
    // }
    // }
    //
    // @Override
    // public Value generateValue(Object... params) {
    // double chance = Math.random();
    // int hit = Arrays.binarySearch(freqs, chance);
    // if (hit < 0) {
    // hit = - hit - 1;
    // }
    // return new Value(colors[hit].name());
    // }
    // };
    //
    // // takes 2 parameters: double minimumSize, double maximumSize
    // private static final AttributeDescriptor screenSize = new
    // AttributeDescriptor("Screen Size", StorageType.STRING) {
    // @Override
    // public Value generateValue(Object... params) {
    // double minSize = (double) params[0];
    // double maxSize = (double) params[1];
    // double result = minSize + Math.random()*(maxSize - minSize);
    // //example of string: "4.1"
    // return new Value(Double.toString(result).substring(0, 3));
    // }
    // };
    //
    // // takes 1 parameter - boolean isSmartphone
    // private static final AttributeDescriptor cost = new
    // AttributeDescriptor("Cost", StorageType.NUMERIC) {
    // // we got 6 cost categories - gonna use it in manufacturer generation
    // private final PhoneCost[] smartCosts = new PhoneCost[]{
    // new PhoneCost(costBounds[0], costBounds[1], 0.05), new
    // PhoneCost(costBounds[1], costBounds[2], 0.35),
    // new PhoneCost(costBounds[2], costBounds[3], 0.55), new
    // PhoneCost(costBounds[3], costBounds[4], 0.75),
    // new PhoneCost(costBounds[4], costBounds[5], 0.9), new
    // PhoneCost(costBounds[5], costBounds[6], 1.0)
    // };
    // private final PhoneCost[] phoneCosts = new PhoneCost[]{
    // new PhoneCost(costBounds[0], costBounds[1], 0.81), new
    // PhoneCost(costBounds[1], costBounds[2], 0.92),
    // new PhoneCost(costBounds[2], costBounds[3], 0.96), new
    // PhoneCost(costBounds[3], costBounds[4], 0.98),
    // new PhoneCost(costBounds[4], costBounds[5], 0.985), new
    // PhoneCost(costBounds[5], costBounds[6], 1.0)
    // };
    // @Override
    // public Value generateValue(Object... params) {
    // boolean smart = (boolean) params[0];
    // PhoneCost[] targetCosts = (smart ? smartCosts : phoneCosts);
    // double chance = Math.random();
    // int hit = Arrays.binarySearch(targetCosts, chance);
    // if (hit < 0) {
    // hit = - hit - 1;
    // }
    // int resultCost = targetCosts[hit].getMinCost()
    // + (int) (Math.random()*(targetCosts[hit].getMaxCost() -
    // targetCosts[hit].getMinCost()));
    // return new Value(Integer.toString(resultCost));
    // }
    // };
    //
    // private static final String[] androidOS = new String[]{
    // "Android 2.1", "Android 2.2", "Android 2.3", "Android 4.0",
    // "Android 4.1", "Android 4.2", "Android 4.3", "Android 4.4",
    // };
    //
    // private static final String[] iOS = new String[]{
    // "iOS 4","iOS 5","iOS 6","iOS 7","iOS 8"
    // };
    //
    // private static final String[] windowsPhoneOS = new String[]{
    // "Windows Phone 7","Windows Phone 7.5",
    // "Windows Phone 8","Windows Phone 8.1",
    // };
    //
    // //takes 2 parameters: String manufactorer, int costCategory
    // private static final AttributeDescriptor operationSystem = new
    // AttributeDescriptor("OS", StorageType.ENUM) {
    // private final double[][] androidFreqs = new double[][]{
    // {0.02, 0.05, 0.15, 0.27, 0.42, 0.87, 0.88, 1.0},
    // {0.01, 0.03, 0.14, 0.28, 0.44, 0.84, 0.88, 1.0},
    // {0.00, 0.01, 0.06, 0.18, 0.32, 0.79, 0.87, 1.0},
    // {0.00, 0.00, 0.05, 0.13, 0.30, 0.68, 0.79, 1.0},
    // {0.00, 0.00, 0.04, 0.03, 0.17, 0.36, 0.47, 1.0},
    // {0.00, 0.03, 0.10, 0.19, 0.26, 0.45, 0.50, 1.0}
    // };
    //
    // private final double[][] iOSFreqs = new double[][]{
    // {0},
    // {0},
    // {0},
    // {0},
    // {0.15, 0.45, 0.68, 1.0, 1.0},
    // {0.00, 0.13, 0.33, 0.6, 1.0},
    // };
    //
    // private final double[][] windowsPhoneFreqs = new double[][]{
    // {0.06, 0.37, 0.52, 1.0},
    // {0.04, 0.35, 0.71, 1.0},
    // {0.00, 0.26, 0.68, 1.0},
    // {0.00, 0.31, 0.74, 1.0},
    // {0.08, 0.33, 0.83, 1.0},
    // {0.5, 0.5, 0.5, 1.0}
    // };
    //
    // @Override
    // public Value generateValue(Object... params) {
    // String[] targetOS;
    // double[] targetFreqs;
    // String manufactorer = (String) params[0];
    // int costCat = (int) params[1];
    // if (manufactorer == "Apple") {
    // targetOS = iOS;
    // targetFreqs = iOSFreqs[costCat];
    // } else {
    // if (Math.random() > 0.05) {
    // targetOS = androidOS;
    // targetFreqs = androidFreqs[costCat];
    // } else {
    // targetOS = windowsPhoneOS;
    // targetFreqs = windowsPhoneFreqs[costCat];
    // }
    // }
    // double chance = Math.random();
    // int hit = Arrays.binarySearch(targetFreqs, chance);
    // if (hit < 0) {
    // hit = - hit - 1;
    // }
    // return new Value(targetOS[hit]);
    // }
    // };
    //
    // //takes 2 parameters: int minRam, int maxRam
    // private static final AttributeDescriptor ram = new
    // AttributeDescriptor("RAM", StorageType.NUMERIC) {
    // @Override
    // public Value generateValue(Object... params) {
    // //TODO interesting RAM generator
    // int minRam = (int) params[0];
    // int maxRam = (int) params[1];
    // return new Value(Integer.toString((int)(minRam
    // + Math.random()*(maxRam - minRam))));
    // }
    // };
    //
    // private static final AttributeDescriptor model = new
    // AttributeDescriptor("Model", StorageType.STRING) {
    // @Override
    // public Value generateValue(Object... params) {
    // return new Value(RandomUtils.getString(4, 15));
    // }
    // };
    //
    // //TODO CPU, built-in memory, sd-card, sim-cards
    //
    // public MobilePhoneTest(int documentsCount, int requestsCount, int
    // reqAttributesCount,
    // Supplier<BitSet> bitSetSupplier) {
    // this.documentsCount = documentsCount;
    // this.requestsCount = requestsCount;
    // this.reqAttributesCount = reqAttributesCount;
    // Map<String, StorageType> indexes = new HashMap<>();
    // indexes.put("Art.", StorageType.STRING);
    // for (AttributeDescriptor desc : attributeDescriptors) {
    // indexes.put(desc.getAttributeName(), desc.getValueType());
    // }
    //
    // simpleEngineConfig = new EngineConfig(indexes, bitSetSupplier, new
    // TinyOptimizer());
    // currentArt = 0;
    // }
    //
    // public EngineConfig getEngineConfig() {
    // return simpleEngineConfig;
    // }
    //
    //
    // public GeneratorBase<Map<String, List<Value>>> getDocumentGenerator() {
    // return new GeneratorBase<Map<String, List<Value>>>(false, documentsCount)
    // {
    // @Override
    // protected Map<String, List<Value>> generateEntity() {
    // Map<String, List<Value>> document = new HashMap<>();
    // document.put("Art.", Arrays.asList(new
    // Value(Integer.toString(currentArt++))));
    // Value smart = isSmartphone.generateValue();
    // boolean isSmart = smart.getValue().equals("true");
    // double minScreen = 2.5;
    // double maxScreen = 5.0;
    //
    // document.put(isSmartphone.getAttributeName(), Arrays.asList(smart));
    // document.put(model.getAttributeName(),
    // Arrays.asList(model.generateValue()));
    // document.put(color.getAttributeName(),
    // Arrays.asList(color.generateValue()));
    // //TODO more complicated screen size generation
    // document.put(screenSize.getAttributeName(),
    // Arrays.asList(screenSize.generateValue(minScreen, maxScreen)));
    //
    // Value phoneCost = cost.generateValue(isSmart);
    // int costCat = getCostCategory(Integer.parseInt(phoneCost.getValue()));
    // document.put(cost.getAttributeName(), Arrays.asList(phoneCost));
    // Value phoneManufactorer = manufacturer.generateValue(isSmart, costCat);
    // document.put(manufacturer.getAttributeName(),
    // Arrays.asList(phoneManufactorer));
    // if (isSmart) {
    // document.put(operationSystem.getAttributeName(),
    // Arrays.asList(operationSystem.generateValue(phoneManufactorer.getValue(),
    // costCat)));
    // int minRam = 128;
    // int maxRam = 4096;
    // document.put(ram.getAttributeName(),
    // Arrays.asList(ram.generateValue(minRam, maxRam)));
    // } else {
    // document.put(operationSystem.getAttributeName(),
    // Arrays.asList(new Value("no OS")));
    // document.put(ram.getAttributeName(), Arrays.asList(new Value("-1")));
    // }
    // return document;
    // }
    // };
    // }
    //
    //
    // public GeneratorBase<Request> getRequestGenerator() {
    // return new GeneratorBase<Request>(false, requestsCount) {
    // @Override
    // protected Request generateEntity() {
    // Request request = Request.build();
    // String[] targetOS;
    // double chance = Math.random();
    // if (chance > 0.66) {
    // targetOS = androidOS;
    // } else {
    // if (chance > 0.33)
    // targetOS = iOS;
    // else
    // targetOS = windowsPhoneOS;
    // }
    //
    // MainColor[] colors = MainColor.values();
    // String mainColor = colors[(int)(Math.random()*colors.length)].name();
    // request.exact(color.getAttributeName(), mainColor);
    //
    // request.exact(isSmartphone.getAttributeName(), "false");
    //
    // String os = targetOS[(int)(Math.random()*targetOS.length)];
    // request.exact(operationSystem.getAttributeName(), os);
    // return request;
    // }
    // };
    // }
    //
    // private int getCostCategory(int cost) {
    // int category;
    // for (category = 1; category < costBounds.length; category++) {
    // if (cost <= costBounds[category]) break;
    // }
    // return category - 1;
    // }
    //
    // @Override
    // public String toString() {
    // return
    // String.format("MobilePhoneTest [%s values, %s documents, %s requests]",
    // attributeDescriptors.length + 1, documentsCount, requestsCount);
    // }

}
