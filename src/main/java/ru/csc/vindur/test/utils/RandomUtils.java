package ru.csc.vindur.test.utils;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.RandomStringUtils;

public class RandomUtils {
    private final static Random random = new Random(0);

    public static void setSeed(long seed) {
        random.setSeed(seed);
    }

    public static <T> T uniformRandomElement(T[] array) {
        int idx = random.nextInt(array.length);
        return array[idx];
    }

    public static <T> T gaussianRandomElement(T[] array, double expectedValue,
            double standartDeviation) {
        double gaussian;
        double halfViewLen = 0.5 / standartDeviation;
        do {
            gaussian = random.nextGaussian();
        } while (Math.abs(gaussian) > halfViewLen);

        gaussian = gaussian * standartDeviation + expectedValue;
        int idx = (int) (gaussian * array.length) % array.length;
        return array[idx];
    }

    public static String getString(int minLen, int maxLen) {
        return RandomStringUtils.randomAlphanumeric(genLen(minLen, maxLen));
    }

    public static int getNumber(int min, int max) {
        if (min == max) {
            return min;
        }
        return min + random.nextInt(max - min);
    }

    private static int genLen(int min, int max) {
        return getNumber(min, max);
    }

    public static <T> T getFrec(Map<T, Double> frequencies) {
        double randomDouble = random.nextDouble();
        double summ = 0;
        for (Entry<T, Double> entry : frequencies.entrySet()) {
            summ += entry.getValue();
            if (summ >= randomDouble) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static Set<Integer> getRandomIndexes(int attributesCount,
            int reqAttributesCount) {
        Set<Integer> result = new TreeSet<>();

        // TODO fix this. It works too long if there is many collisions
        while (result.size() < reqAttributesCount) {
            result.add(random.nextInt(attributesCount));
        }
        return result;
    }

    public static Set<String> getRandomStrings(List<String> vals,
            int reqAttributesCount) {
        Set<String> result = new TreeSet<>();
        int len = vals.size();
        while (result.size() < reqAttributesCount) {
            result.add(vals.get(random.nextInt(len)));
        }
        return result;
    }

}
