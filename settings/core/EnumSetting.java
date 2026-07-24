/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.settings.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Consumer;

public class EnumSetting<T extends Enum<T>> extends Setting<T> {
    private final Class<T> enumClass;

    protected EnumSetting(
        String name,
        String description,
        T defaultValue,
        Consumer<T> onChanged,
        Consumer<Setting<T>> onModuleActivated,
        IVisible visible
    ) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
        if (defaultValue == null) throw new IllegalArgumentException("Enum setting requires a default value");
        enumClass = defaultValue.getDeclaringClass();
    }

    @Override
    protected T parseImpl(String text) {
        if (text == null) return null;
        for (T constant : enumClass.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(text.trim())) return constant;
        }
        return null;
    }

    @Override
    protected CompoundTag save(CompoundTag tag) {
        tag.putString("value", value.name());
        return tag;
    }

    @Override
    protected T load(CompoundTag tag) {
        T parsed = parseImpl(tag.getString("value").orElse(""));
        return parsed == null ? defaultValue : parsed;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(value.name());
    }

    @Override
    public boolean fromJson(JsonElement json) {
        return json != null && json.isJsonPrimitive() && parse(json.getAsString());
    }

    public static class Builder<T extends Enum<T>> extends SettingBuilder<Builder<T>, T, EnumSetting<T>> {
        public Builder() {
            super(null);
        }

        @Override
        public EnumSetting<T> build() {
            return new EnumSetting<>(name, description, defaultValue, onChanged, onModuleActivated, visible);
        }
    }
}
