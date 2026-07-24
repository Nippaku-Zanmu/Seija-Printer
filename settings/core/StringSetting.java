/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.settings.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class StringSetting extends Setting<String> {
    private final Predicate<String> validator;

    protected StringSetting(
        String name,
        String description,
        String defaultValue,
        Consumer<String> onChanged,
        Consumer<Setting<String>> onModuleActivated,
        IVisible visible,
        Predicate<String> validator
    ) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
        this.validator = validator;
    }

    @Override
    protected String parseImpl(String text) {
        return text;
    }

    @Override
    protected boolean isValueValid(String value) {
        return value != null && (validator == null || validator.test(value));
    }

    @Override
    protected CompoundTag save(CompoundTag tag) {
        tag.putString("value", value);
        return tag;
    }

    @Override
    protected String load(CompoundTag tag) {
        return tag.getString("value").orElse(defaultValue);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(value);
    }

    @Override
    public boolean fromJson(JsonElement json) {
        return json != null && json.isJsonPrimitive() && set(json.getAsString());
    }

    public static class Builder extends SettingBuilder<Builder, String, StringSetting> {
        private Predicate<String> validator;

        public Builder() {
            super("");
        }

        public Builder filter(Predicate<String> validator) {
            this.validator = validator;
            return this;
        }

        @Override
        public StringSetting build() {
            return new StringSetting(name, description, defaultValue, onChanged, onModuleActivated, visible, validator);
        }
    }
}
