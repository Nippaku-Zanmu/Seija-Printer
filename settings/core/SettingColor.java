/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.settings.core;

public class SettingColor extends Color {
    public boolean rainbow;

    public SettingColor(int r, int g, int b) {
        this(r, g, b, 255, false);
    }

    public SettingColor(int r, int g, int b, int a) {
        this(r, g, b, a, false);
    }

    public SettingColor(int r, int g, int b, int a, boolean rainbow) {
        super(r, g, b, a);
        this.rainbow = rainbow;
    }

    public SettingColor(SettingColor color) {
        this(color.r, color.g, color.b, color.a, color.rainbow);
    }

    @Override
    public SettingColor a(int alpha) {
        return new SettingColor(r, g, b, alpha, rainbow);
    }

    @Override
    public boolean equals(Object object) {
        return super.equals(object) && object instanceof SettingColor color && rainbow == color.rainbow;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Boolean.hashCode(rainbow);
    }
}
