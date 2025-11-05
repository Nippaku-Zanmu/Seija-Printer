/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.util;


public class SeijaTimer {
    private long time = -1L;
    private Runnable onReset;
    public SeijaTimer(Runnable onReset){
        this.onReset = onReset;
    }
    public SeijaTimer() {

    }

    public long getMsTime() {
        return time;
    }

    public long getTime() {
        return time;
    }



    public boolean passedS(double s) {
        return this.getMs(System.currentTimeMillis() - this.time) >= (long) (s * 1000.0);
    }

    public boolean passedM(double m) {
        return this.getMs(System.currentTimeMillis() - this.time) >= (long) (m * 1000.0 * 60.0);
    }

    public boolean passedDms(double dms) {
        return this.getMs(System.currentTimeMillis() - this.time) >= (long) (dms * 10.0);
    }

    public boolean passedDs(double ds) {
        return this.getMs(System.currentTimeMillis() - this.time) >= (long) (ds * 100.0);
    }

    public boolean passedMs(long ms) {
        return this.getMs(System.currentTimeMillis() - this.time) >= ms;
    }

    public boolean passedMs(double ms) {
        return this.getMs(System.currentTimeMillis() - this.time) >= ms;
    }



    public SeijaTimer reset() {
        this.time = System.currentTimeMillis();
        if (onReset!=null)onReset.run();
        return this;
    }

//    public void setMs(long ms) {
//        this.time = System.nanoTime() - ms * 1000000L;
//    }

    public boolean sleep(long l) {
        if (System.nanoTime() / 1000000L - l >= l) {
            this.reset();
            return true;
        }
        return false;
    }

    public long getPassedTimeMs() {
        return this.getMs(System.currentTimeMillis() - this.time);
    }


    public long getMs(long time) {
        return time;
    }

    public boolean passed(int ms) {
        return ((System.currentTimeMillis() - this.time) >= ms);
    }

    public boolean passed(double ms) {
        return ((System.currentTimeMillis() - this.time) >= ms);
    }

//    public boolean passedMs(@NotNull Unit toLong) {
//        return false;
//    }
}

