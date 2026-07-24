package com.kijinseija.seija_printer.settings.widgets.input;

import com.kijinseija.seija_printer.settings.obj.DoubleRange;

/** UI-independent editor model for a double range. */
public final class WDoubleRangeEdit {
    private double valueMin;
    private double valueMax;
    private final double min;
    private final double max;
    public Runnable action;
    public Runnable actionOnRelease;

    public WDoubleRangeEdit(double valueMin, double valueMax, double min, double max,
                            double sliderMin, double sliderMax, int decimalPlaces, boolean noSlider) {
        this.min = min;
        this.max = max;
        set(valueMin, valueMax);
    }

    public double[] get() { return new double[]{valueMin, valueMax}; }
    public DoubleRange getDoubleRange() { return new DoubleRange(valueMin, valueMax); }

    public void set(DoubleRange range) {
        if (range != null) set(range.value1, range.value2);
    }

    public void set(double first, double second) {
        double nextMin = Math.max(min, Math.min(max, Math.min(first, second)));
        double nextMax = Math.max(nextMin, Math.min(max, Math.max(first, second)));
        boolean changed = nextMin != valueMin || nextMax != valueMax;
        valueMin = nextMin;
        valueMax = nextMax;
        if (changed && action != null) action.run();
    }
}
