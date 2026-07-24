package com.kijinseija.seija_printer.settings.widgets.input;

import java.util.function.BiConsumer;

/** UI-independent range model shared by the native settings screen. */
public class WRangeSlider {
    protected double value1;
    protected double value2;
    protected final double min;
    protected final double max;
    public Runnable action;
    public Runnable actionOnRelease;

    public WRangeSlider(double value1, double value2, double min, double max) {
        this.min = Math.min(min, max);
        this.max = Math.max(min, max);
        set(value1, value2);
    }

    public void set(double value1, double value2) {
        this.value1 = clamp(value1);
        this.value2 = clamp(value2);
        if (this.value1 > this.value2) {
            double swap = this.value1;
            this.value1 = this.value2;
            this.value2 = swap;
        }
        if (action != null) action.run();
    }

    public void setMin(double value) { set(value, value2); }
    public void setMax(double value) { set(value1, value); }
    public double[] get() { return new double[]{value1, value2}; }

    private double clamp(double value) {
        return Math.max(min, Math.min(max, value));
    }
}
