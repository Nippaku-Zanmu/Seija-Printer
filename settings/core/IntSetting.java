/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.settings.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Consumer;

public class IntSetting extends Setting<Integer> {
    public final int min;
    public final int max;
    public final int sliderMin;
    public final int sliderMax;
    public final boolean onSliderRelease;
    public final boolean noSlider;

    protected IntSetting(
        String name,
        String description,
        Integer defaultValue,
        Consumer<Integer> onChanged,
        Consumer<Setting<Integer>> onModuleActivated,
        IVisible visible,
        int min,
        int max,
        int sliderMin,
        int sliderMax,
        boolean onSliderRelease,
        boolean noSlider
    ) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
        this.min = min;
        this.max = max;
        this.sliderMin = sliderMin;
        this.sliderMax = sliderMax;
        this.onSliderRelease = onSliderRelease;
        this.noSlider = noSlider;
    }

    @Override
    protected Integer parseImpl(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    @Override
    protected boolean isValueValid(Integer value) {
        return value != null && value >= min && value <= max;
    }

    @Override
    protected CompoundTag save(CompoundTag tag) {
        tag.putInt("value", value);
        return tag;
    }

    @Override
    protected Integer load(CompoundTag tag) {
        return tag.getInt("value").orElse(defaultValue);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(value);
    }

    @Override
    public boolean fromJson(JsonElement json) {
        return json != null && json.isJsonPrimitive() && set(json.getAsInt());
    }

    public static class Builder extends SettingBuilder<Builder, Integer, IntSetting> {
        protected int min = Integer.MIN_VALUE;
        protected int max = Integer.MAX_VALUE;
        protected int sliderMin = 0;
        protected int sliderMax = 10;
        protected boolean onSliderRelease;
        protected boolean noSlider;

        public Builder() {
            super(0);
        }

        public Builder defaultValue(int value) {
            this.defaultValue = value;
            return this;
        }

        public Builder min(int min) {
            this.min = min;
            return this;
        }

        public Builder max(int max) {
            this.max = max;
            return this;
        }

        public Builder range(int min, int max) {
            this.min = Math.min(min, max);
            this.max = Math.max(min, max);
            return this;
        }

        public Builder sliderMin(int sliderMin) {
            this.sliderMin = sliderMin;
            return this;
        }

        public Builder sliderMax(int sliderMax) {
            this.sliderMax = sliderMax;
            return this;
        }

        public Builder sliderRange(int sliderMin, int sliderMax) {
            this.sliderMin = Math.min(sliderMin, sliderMax);
            this.sliderMax = Math.max(sliderMin, sliderMax);
            return this;
        }

        public Builder onSliderRelease() {
            onSliderRelease = true;
            return this;
        }

        public Builder noSlider() {
            noSlider = true;
            return this;
        }

        @Override
        public IntSetting build() {
            return new IntSetting(
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
                noSlider
            );
        }
    }
}
