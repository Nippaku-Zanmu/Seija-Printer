/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.settings.obj;

import java.util.concurrent.ThreadLocalRandom;

public class DoubleRange {
    public double value1;
    public double value2;
    private double random = 0;

    public DoubleRange(double value1, double value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    public DoubleRange() {
        value1 = value2 = 0;
    }

    public double getMin() {
        return Math.min(value1, value2);
    }

    public double getMax() {
        return Math.max(value1, value2);
    }

    public boolean isInRange(double value) {
        return value <= getMax() && value >= getMin();
    }
    public double getCurrentRandom(){
        return isInRange(random)?random:nextRandom();
    }
    public double nextRandom() {
        this.random = getRandomInRangeClosed(getMin(), getMax());
        return this.random;
    }

    public static double getRandomInRangeClosed(double v1, double v2) {
        double min = Math.min(v1, v2);
        double max = Math.max(v1, v2);
        // 加入边界补偿(ulp_ 返回值[v1,v2]不加是[v1,v2)
        return ThreadLocalRandom.current().nextDouble(min, max + Math.ulp(max));
    }
}
