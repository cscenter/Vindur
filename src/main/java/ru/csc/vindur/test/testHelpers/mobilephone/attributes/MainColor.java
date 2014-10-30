package ru.csc.vindur.test.testHelpers.mobilephone.attributes;

/**
 * Created by Pavel Chursin on 29.10.2014.
 */
public enum MainColor{
    BLACK(0.25),
    GRAY(0.45),
    WHITE(0.65),
    RED(0.7),
    GREEN(0.75),
    BLUE(0.8),
    PINK(0.85),
    YELLOW(0.9),
    PURPLE(0.95),
    ORANGE(1.0);
    private double frequency;
    private MainColor(double frequency) {
        this.frequency = frequency;
    }

    public double getFrequency() {
        return frequency;
    }
}
