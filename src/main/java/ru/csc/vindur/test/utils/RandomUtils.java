package ru.csc.vindur.test.utils;

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
	
	public static <T> T gaussianRandomElement(T[] array, double expectedValue, double standartDeviation) {
		double gaussian;
		double halfViewLen = 0.5 / standartDeviation;
		do {
			gaussian = random.nextGaussian();
		} while(Math.abs(gaussian) > halfViewLen);
		
		gaussian = gaussian * standartDeviation + expectedValue;
		int idx = (int)(gaussian * array.length) % array.length;
		return array[idx];
	}

	public static String getString(int minLen, int maxLen) {
		return RandomStringUtils.randomAlphanumeric(genLen(minLen, maxLen));
	}

	public static String getNumericString(int minLen, int maxLen) {
		return RandomStringUtils.randomNumeric(genLen(minLen, maxLen));
	}
	
	private static int genLen(int minLen, int maxLen) {
		if(minLen == maxLen) {
			return minLen;
		}
		return minLen + random.nextInt(maxLen - minLen);
	}

	public static <T> T getFrec(Map<T, Double> frequencies) {
		double randomDouble = random.nextDouble();
		double summ = 0;
		for(Entry<T, Double> entry: frequencies.entrySet()) {
			summ += entry.getValue();
			if(summ >= randomDouble) {
				return entry.getKey();
			}
		}
		return null;
	}

	public static Set<Integer> getRandomIndexes(int attributesCount, int reqAttributesCount) {
		Set<Integer> result = new TreeSet<>();
		
		// TODO fix this. It works too long if there is many collisions
		while(result.size() < reqAttributesCount) {
			result.add(random.nextInt(attributesCount));
		}
		return result;
	}
}
