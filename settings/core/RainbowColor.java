package com.kijinseija.seija_printer.settings.core;

/** Smooth cyclic color used by the printer's placement preview. */
public final class RainbowColor extends Color {
    private double speed = 0.01;
    private double phase;

    public RainbowColor() {
        super(255, 0, 0, 255);
    }

    public void setSpeed(double speed) {
        this.speed = Math.max(0.0, speed);
    }

    public void getNext() {
        phase = (phase + speed) % 1.0;
        int rgb = java.awt.Color.HSBtoRGB((float) phase, 0.8f, 1.0f);
        r = (rgb >>> 16) & 0xff;
        g = (rgb >>> 8) & 0xff;
        b = rgb & 0xff;
    }
}
