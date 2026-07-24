/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.settings.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Consumer;

public class DoubleSetting extends Setting<Double> {
    public final double min;
    public final double max;
    public final double sliderMin;
    public final double sliderMax;
    public final boolean onSliderRelease;
    public final int decimalPlaces;
    public final boolean noSlider;

    protected DoubleSetting(
        String name,
        String description,
        Double defaultValue,
        Consumer<Double> onChanged,
        Consumer<Setting<Double>> onModuleActivated,
        IVisible visible,
        double min,
        double max,
        double sliderMin,
        double sliderMax,
        boolean onSliderRelease,
        int decimalPlaces,
        boolean noSlider
    ) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
        this.min = min;
        this.max = max;
        this.sliderMin = sliderMin;
        this.sliderMax = sliderMax;
        this.onSliderRelease = onSliderRelease;
        this.decimalPlaces = decimalPlaces;
        this.noSlider = noSlider;
    }

    @Override
    protected Double parseImpl(String text) {
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    @Override
    protected boolean isValueValid(Double value) {
        return value != null && !value.isNaN() && value >= min && value <= max;
    }

    @Override
    protected CompoundTag save(CompoundTag tag) {
        tag.putDouble("value", value);
        return tag;
    }

    @Override
    protected Double load(CompoundTag tag) {
        return tag.getDouble("value").orElse(defaultValue);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(value);
    }

    @Override
    public boolean fromJson(JsonElement json) {
        return json != null && json.isJsonPrimitive() && set(json.getAsDouble());
    }

    public static class Builder extends SettingBuilder<Builder, Double, DoubleSetting> {
        protected double min = Double.NEGATIVE_INFINITY;
        protected double max = Double.POSITIVE_INFINITY;
        protected double sliderMin = 0;
        protected double sliderMax = 10;
        protected boolean onSliderRelease;
        protected int decimalPlaces = 3;
        protected boolean noSlider;

        public Builder() {
            super(0.0);
        }

        public Builder defaultValue(double value) {
            this.defaultValue = value;
            return this;
        }

        public Builder min(double min) {
            this.min = min;
            return this;
        }

        public Builder max(double max) {
            this.max = max;
            return this;
        }

        public Builder range(double min, double max) {
            this.min = Math.min(min, max);
            this.max = Math.max(min, max);
            return this;
        }

        public Builder sliderMin(double sliderMin) {
            this.sliderMin = sliderMin;
            return this;
        }

        public Builder sliderMax(double sliderMax) {
            this.sliderMax = sliderMax;
            return this;
        }

        public Builder sliderRange(double sliderMin, double sliderMax) {
            this.sliderMin = Math.min(sliderMin, sliderMax);
            this.sliderMax = Math.max(sliderMin, sliderMax);
            return this;
        }

        public Builder onSliderRelease() {
            onSliderRelease = true;
            return this;
        }

        public Builder decimalPlaces(int decimalPlaces) {
            this.decimalPlaces = Math.max(0, decimalPlaces);
            return this;
        }

        public Builder noSlider() {
            noSlider = true;
            return this;
        }

        @Override
        public DoubleSetting build() {
            return new DoubleSetting(
                name,
                description,
                defaultValue,
                onChanged,
                onModuleActivated,
                visible,
                min,
                max,
                Math.max(sliderMin, min),
                Math.min(sliderMax, max),
                onSliderRelease,
                decimalPlaces,
                noSlider
            );
        }
    }
}
