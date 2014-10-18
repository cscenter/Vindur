package ru.csc.vindur.test.utils;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang.RandomStringUtils;

public class RandomUtils {

	public static <T> T uniformRandomElement(T[] array) {
		int idx = ThreadLocalRandom.current().nextInt(array.length);
		return array[idx];
	}
	
	public static <T> T gaussianRandomElement(T[] array) {
		double gaussian;
		do {
			gaussian = ThreadLocalRandom.current().nextGaussian();
		//99.7% that Math.abs(gaussian) <= 3
		} while(Math.abs(gaussian) > 3);
		
		gaussian = (gaussian + 3) / 6;
		int idx = (int)(gaussian * array.length);
		
		// to be sure 
		// TODO investigate about accuracy  
		if(idx < 0) {
			idx = 0;
		} else {
			if(idx >= array.length) {
				idx = array.length - 1;
			}
		}
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
		return minLen + ThreadLocalRandom.current().nextInt(maxLen - minLen);
	}

	public static <T> T getFrec(Map<T, Double> frequencies) {
		double random = ThreadLocalRandom.current().nextDouble();
		double summ = 0;
		for(Entry<T, Double> entry: frequencies.entrySet()) {
			summ += entry.getValue();
			if(summ >= random) {
				return entry.getKey();
			}
		}
		return null;
	}

	public static Set<Integer> getRandomIndexes(int attributesCount, int reqAttributesCount) {
		Set<Integer> result = new TreeSet<>();
		
		// TODO fix this. It works too long if there is many collisions
		while(result.size() < reqAttributesCount) {
			result.add(ThreadLocalRandom.current().nextInt(attributesCount));
		}
		return result;
	}
}
