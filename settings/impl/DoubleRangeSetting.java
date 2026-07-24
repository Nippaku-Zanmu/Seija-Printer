/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */



package com.kijinseija.seija_printer.settings.impl;

import java.util.function.Consumer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.kijinseija.seija_printer.settings.obj.DoubleRange;
import com.kijinseija.seija_printer.settings.core.IVisible;
import com.kijinseija.seija_printer.settings.core.Setting;
import net.minecraft.nbt.CompoundTag;

public class DoubleRangeSetting extends Setting<DoubleRange> {
    public final double min, max;
    public final double sliderMin, sliderMax;
    public final boolean onSliderRelease;
    public final int decimalPlaces;
    public final boolean noSlider;

    private DoubleRangeSetting(String name, String description, DoubleRange defaultValue, Consumer<DoubleRange> onChanged, Consumer<Setting<DoubleRange>> onModuleActivated, IVisible visible, double min, double max, double sliderMin, double sliderMax, boolean onSliderRelease, int decimalPlaces, boolean noSlider) {
        super(name, description, new DoubleRange(defaultValue.value1, defaultValue.value2), onChanged, onModuleActivated, visible);
        value = new DoubleRange(defaultValue.value1, defaultValue.value2);

        this.min = min;
        this.max = max;
        this.sliderMin = sliderMin;
        this.sliderMax = sliderMax;
        this.decimalPlaces = decimalPlaces;
        this.onSliderRelease = onSliderRelease;
        this.noSlider = noSlider;
    }

    @Override
    protected void resetImpl() {
        value = new DoubleRange(defaultValue.value1, defaultValue.value2);
    }

    @Override
    protected DoubleRange parseImpl(String str) {
        try {
            String[] split = str.split(" ");
            if (split.length==2){
                return new DoubleRange(Double.parseDouble(split[0].trim())
                    ,Double.parseDouble(split[1].trim()));
            }

            return null;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    @Override
    protected boolean isValueValid(DoubleRange value) {
        return value.value1 >= min && value.value1 <= max && value.value2 >= min && value.value2 <= max;
    }

    @Override
    protected CompoundTag save(CompoundTag tag) {
        tag.putDouble("value1", get().value1);
        tag.putDouble("value2", get().value2);
        return tag;
    }

    @Override
    public DoubleRange load(CompoundTag tag) {
        DoubleRange doubleRange = new DoubleRange(tag.getDouble("value1").orElse(0d),tag.getDouble("value2").orElse(0d));
        set(doubleRange);
        return get();
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.add("value1", new JsonPrimitive(get().value1));
        json.add("value2", new JsonPrimitive(get().value2));
        return json;
    }

    @Override
    public boolean fromJson(JsonElement json) {
        if (json == null || !json.isJsonObject()) return false;
        JsonObject object = json.getAsJsonObject();
        if (!object.has("value1") || !object.has("value2")) return false;
        return set(new DoubleRange(object.get("value1").getAsDouble(), object.get("value2").getAsDouble()));
    }

    public static class Builder extends SettingBuilder<Builder, DoubleRange, DoubleRangeSetting> {
        public double min = Double.NEGATIVE_INFINITY, max = Double.POSITIVE_INFINITY;
        public double sliderMin = 0, sliderMax = 10;
        public boolean onSliderRelease = false;
        public int decimalPlaces = 3;
        public boolean noSlider = false;

        public Builder() {
            super(new DoubleRange());
        }

        public Builder defaultValue(DoubleRange defaultValue) {
            this.defaultValue = new DoubleRange(defaultValue.value1, defaultValue.value2);
            return this;
        }
        public Builder defaultValue(double v1 ,double v2) {
            this.defaultValue.value1 = v1;
            this.defaultValue.value2 = v2;
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
        public Builder defaultV1(double v1) {
            this.defaultValue.value1 = v1;
            return this;
        }
        public Builder defaultV2(double v2) {
            this.defaultValue.value2 = v2;
            return this;
        }

        public Builder range(double min, double max) {
            this.min = Math.min(min, max);
            this.max = Math.max(min, max);
            return this;
        }

        public Builder sliderMin(double min) {
            sliderMin = min;
            return this;
        }

        public Builder sliderMax(double max) {
            sliderMax = max;
            return this;
        }

        public Builder sliderRange(double min, double max) {
            sliderMin = min;
            sliderMax = max;
            return this;
        }

        public Builder onSliderRelease() {
            onSliderRelease = true;
            return this;
        }

        public Builder decimalPlaces(int decimalPlaces) {
            this.decimalPlaces = decimalPlaces;
            return this;
        }

        public Builder noSlider() {
            noSlider = true;
            return this;
        }

        public DoubleRangeSetting build() {
            DoubleRange range = new DoubleRange(defaultValue.value1, defaultValue.value2);
            return new DoubleRangeSetting(name, description, range, onChanged, onModuleActivated, visible, min, max, Math.max(sliderMin, min), Math.min(sliderMax, max), onSliderRelease, decimalPlaces, noSlider);
        }
    }
}
