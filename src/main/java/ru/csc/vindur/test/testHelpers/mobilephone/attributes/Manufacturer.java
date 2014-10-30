package ru.csc.vindur.test.testHelpers.mobilephone.attributes;

/**
 * Created by Pavel Chursin on 29.10.2014.
 */
public class Manufacturer implements Comparable<Double>{
    private String name;
    private double frequency;
    public Manufacturer(String name, double frequency) {
        this.name = name;
        this.frequency = frequency;
    }

    public String getName() {
        return name;
    }

    public double getFrequency() {
        return frequency;
    }

    @Override
    public int compareTo(Double o) {
        return Double.compare(frequency, o);
    }
}
