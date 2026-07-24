package com.kijinseija.seija_printer.settings.widgets.input;

/** UI-independent editor model for an integer range. */
public final class WIntRangeEdit {
    private int valueMin;
    private int valueMax;
    private final int min;
    private final int max;
    public Runnable action;
    public Runnable actionOnRelease;

    public WIntRangeEdit(int valueMin, int valueMax, int min, int max,
                         int sliderMin, int sliderMax, int decimalPlaces, boolean noSlider) {
        this.min = min;
        this.max = max;
        set(valueMin, valueMax);
    }

    public int[] get() { return new int[]{valueMin, valueMax}; }

    public void set(int first, int second) {
        int nextMin = Math.max(min, Math.min(max, Math.min(first, second)));
        int nextMax = Math.max(nextMin, Math.min(max, Math.max(first, second)));
        boolean changed = nextMin != valueMin || nextMax != valueMax;
        valueMin = nextMin;
        valueMax = nextMax;
        if (changed && action != null) action.run();
    }
}
