package ru.csc.vindur.test;

import java.util.BitSet;
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

	public static BitSet getBitSet(int size) {
	    BitSet result = new BitSet(size);
	    for (int i = 0; i < size; i ++) {
	        if(baseRandom.nextBoolean()) {
	        	result.set(i);
	        }
	    }
	    return result;
	}
	
	public static BitSet getBitSet(int size, int cardinality) {
	    if (0 > cardinality || cardinality > size) {
	    	throw new IllegalArgumentException();
	    }
	    BitSet result = new BitSet(size);
	    int[] chosen = new int[cardinality];
	    int i;
	    for (i = 0; i < cardinality; i ++) {
	        chosen[i] = i;
	        result.set(i);
	    }
	    for (; i < size; i ++) {
	        int j = baseRandom.nextInt(i+1);
	        if (j < cardinality) {
	            result.clear(chosen[j]);
	            result.set(i);
	            chosen[j] = i;
	        }
	    }
	    return result;
	}
}
