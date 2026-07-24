/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.settings.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Consumer;

public class ColorSetting extends Setting<SettingColor> {
    protected ColorSetting(
        String name,
        String description,
        SettingColor defaultValue,
        Consumer<SettingColor> onChanged,
        Consumer<Setting<SettingColor>> onModuleActivated,
        IVisible visible
    ) {
        super(name, description, new SettingColor(defaultValue), onChanged, onModuleActivated, visible);
        value = new SettingColor(defaultValue);
    }

    @Override
    protected SettingColor parseImpl(String text) {
        if (text == null) return null;
        String[] values = text.split(",");
        if (values.length < 3 || values.length > 5) return null;
        try {
            int alpha = values.length >= 4 ? Integer.parseInt(values[3].trim()) : 255;
            boolean rainbow = values.length == 5 && Boolean.parseBoolean(values[4].trim());
            return new SettingColor(
                Integer.parseInt(values[0].trim()),
                Integer.parseInt(values[1].trim()),
                Integer.parseInt(values[2].trim()),
                alpha,
                rainbow
            );
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    @Override
    protected void resetImpl() {
        value = new SettingColor(defaultValue);
    }

    @Override
    protected CompoundTag save(CompoundTag tag) {
        tag.putInt("r", value.r);
        tag.putInt("g", value.g);
        tag.putInt("b", value.b);
        tag.putInt("a", value.a);
        tag.putBoolean("rainbow", value.rainbow);
        return tag;
    }

    @Override
    protected SettingColor load(CompoundTag tag) {
        return new SettingColor(
            tag.getInt("r").orElse(defaultValue.r),
            tag.getInt("g").orElse(defaultValue.g),
            tag.getInt("b").orElse(defaultValue.b),
            tag.getInt("a").orElse(defaultValue.a),
            tag.getBoolean("rainbow").orElse(defaultValue.rainbow)
        );
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.add("r", new JsonPrimitive(value.r));
        json.add("g", new JsonPrimitive(value.g));
        json.add("b", new JsonPrimitive(value.b));
        json.add("a", new JsonPrimitive(value.a));
        json.add("rainbow", new JsonPrimitive(value.rainbow));
        return json;
    }

    @Override
    public boolean fromJson(JsonElement json) {
        if (json == null || !json.isJsonObject()) return false;
        JsonObject object = json.getAsJsonObject();
        if (!object.has("r") || !object.has("g") || !object.has("b")) return false;
        return set(new SettingColor(
            object.get("r").getAsInt(),
            object.get("g").getAsInt(),
            object.get("b").getAsInt(),
            object.has("a") ? object.get("a").getAsInt() : 255,
            object.has("rainbow") && object.get("rainbow").getAsBoolean()
        ));
    }

    public static class Builder extends SettingBuilder<Builder, SettingColor, ColorSetting> {
        public Builder() {
            super(new SettingColor(255, 255, 255));
        }

        @Override
        public Builder defaultValue(SettingColor color) {
            defaultValue = new SettingColor(color);
            return this;
        }

        @Override
        public ColorSetting build() {
            return new ColorSetting(name, description, defaultValue, onChanged, onModuleActivated, visible);
        }
    }
}
