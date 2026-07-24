/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.settings.core;

import java.util.Objects;

public class Color {
    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color ORANGE = new Color(255, 165, 0);

    public int r;
    public int g;
    public int b;
    public int a;

    public Color(int r, int g, int b) {
        this(r, g, b, 255);
    }

    public Color(int r, int g, int b, int a) {
        this.r = clamp(r);
        this.g = clamp(g);
        this.b = clamp(b);
        this.a = clamp(a);
    }

    protected Color(Color color) {
        this(color.r, color.g, color.b, color.a);
    }

    public Color a(int alpha) {
        return new Color(r, g, b, alpha);
    }

    public int argb() {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    protected static int clamp(int component) {
        return Math.max(0, Math.min(255, component));
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Color color)) return false;
        return r == color.r && g == color.g && b == color.b && a == color.a;
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, g, b, a);
    }

    @Override
    public String toString() {
        return r + "," + g + "," + b + "," + a;
    }
}
