package ru.csc.vindur.test.testHelpers.mobilephone.attributes;

/**
 * Created by Pavel Chursin on 29.10.2014.
 */
public class PhoneCost implements Comparable<Double>{
    private int minCost;
    private int maxCost;
    private double frequency;
    public PhoneCost(int minCost, int maxCost, double frequency) {
        this.minCost = minCost;
        this.maxCost = maxCost;
        this.frequency = frequency;
    }

    public int getMinCost() {
        return minCost;
    }

    public int getMaxCost() {
        return maxCost;
    }

    public double getFrequency() {
        return frequency;
    }

    @Override
    public int compareTo(Double o) {
        return Double.compare(frequency, o);
    }
}
