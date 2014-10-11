package ru.csc.vindur.test;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang.RandomStringUtils;

class RandomUtils {
	private static Random baseRandom = ThreadLocalRandom.current();
	
	public static String getString(int maxLen, int minLen) {
		int len = baseRandom.nextInt(maxLen - minLen) + minLen;
		return getString(len);
	}

	public static String getString(int len) {
		return RandomStringUtils.randomAlphanumeric(len);
	}
}
