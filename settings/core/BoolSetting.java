/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.settings.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Consumer;

public class BoolSetting extends Setting<Boolean> {
    protected BoolSetting(
        String name,
        String description,
        Boolean defaultValue,
        Consumer<Boolean> onChanged,
        Consumer<Setting<Boolean>> onModuleActivated,
        IVisible visible
    ) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    }

    @Override
    protected Boolean parseImpl(String text) {
        if ("true".equalsIgnoreCase(text)) return true;
        if ("false".equalsIgnoreCase(text)) return false;
        return null;
    }

    @Override
    protected CompoundTag save(CompoundTag tag) {
        tag.putBoolean("value", value);
        return tag;
    }

    @Override
    protected Boolean load(CompoundTag tag) {
        return tag.getBoolean("value").orElse(defaultValue);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(value);
    }

    @Override
    public boolean fromJson(JsonElement json) {
        return json != null && json.isJsonPrimitive() && set(json.getAsBoolean());
    }

    public static class Builder extends SettingBuilder<Builder, Boolean, BoolSetting> {
        public Builder() {
            super(false);
        }

        public Builder defaultValue(boolean value) {
            this.defaultValue = value;
            return this;
        }

        @Override
        public BoolSetting build() {
            return new BoolSetting(name, description, defaultValue, onChanged, onModuleActivated, visible);
        }
    }
}
