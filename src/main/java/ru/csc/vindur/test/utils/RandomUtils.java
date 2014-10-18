package ru.csc.vindur.test.utils;

import java.util.concurrent.ThreadLocalRandom;

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
		return array[idx];
	}
}
