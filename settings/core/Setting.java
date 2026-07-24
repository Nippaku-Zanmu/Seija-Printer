/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.settings.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class Setting<T> {
    public final String name;
    public final String description;

    public final T defaultValue;
    protected T value;

    private final Consumer<T> onChanged;
    private final Consumer<Setting<T>> onModuleActivated;
    private final IVisible visible;
    private boolean changed;

    protected Setting(
        String name,
        String description,
        T defaultValue,
        Consumer<T> onChanged,
        Consumer<Setting<T>> onModuleActivated,
        IVisible visible
    ) {
        this.name = name == null ? "" : name;
        this.description = description == null ? "" : description;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.onChanged = onChanged;
        this.onModuleActivated = onModuleActivated;
        this.visible = visible == null ? () -> true : visible;
    }

    public final T get() {
        return value;
    }

    public boolean set(T newValue) {
        if (!isValueValid(newValue)) return false;

        boolean valueChanged = !Objects.deepEquals(value, newValue);
        value = newValue;
        changed |= valueChanged;

        if (valueChanged && onChanged != null) onChanged.accept(value);
        return true;
    }

    public final void reset() {
        T previous = value;
        resetImpl();
        boolean valueChanged = !Objects.deepEquals(previous, value);
        changed |= valueChanged;
        if (valueChanged && onChanged != null) onChanged.accept(value);
    }

    protected void resetImpl() {
        value = defaultValue;
    }

    public final boolean parse(String text) {
        T parsed = parseImpl(text);
        return parsed != null && set(parsed);
    }

    protected abstract T parseImpl(String text);

    protected boolean isValueValid(T value) {
        return value != null;
    }

    public final boolean isVisible() {
        return visible.isVisible();
    }

    public boolean wasChanged() {
        return changed || !Objects.deepEquals(value, defaultValue);
    }

    public final void onModuleActivated() {
        if (onModuleActivated != null) onModuleActivated.accept(this);
    }

    public final CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("name", name);
        return save(tag);
    }

    public final boolean fromTag(CompoundTag tag) {
        if (tag == null) return false;
        T loaded = load(tag);
        return loaded != null && set(loaded);
    }

    protected CompoundTag save(CompoundTag tag) {
        if (value != null) tag.putString("value", value.toString());
        return tag;
    }

    protected T load(CompoundTag tag) {
        return parseImpl(tag.getString("value").orElse(""));
    }

    public JsonElement toJson() {
        return value == null ? JsonNull.INSTANCE : new JsonPrimitive(value.toString());
    }

    public boolean fromJson(JsonElement json) {
        return json != null && !json.isJsonNull() && parse(json.getAsString());
    }

    public abstract static class SettingBuilder<
        B extends SettingBuilder<B, T, S>,
        T,
        S extends Setting<T>
    > {
        protected String name = "";
        protected String description = "";
        protected T defaultValue;
        protected Consumer<T> onChanged;
        protected Consumer<Setting<T>> onModuleActivated;
        protected IVisible visible = () -> true;

        protected SettingBuilder(T defaultValue) {
            this.defaultValue = defaultValue;
        }

        @SuppressWarnings("unchecked")
        protected final B self() {
            return (B) this;
        }

        public B name(String name) {
            this.name = name;
            return self();
        }

        public B description(String description) {
            this.description = description;
            return self();
        }

        public B defaultValue(T defaultValue) {
            this.defaultValue = defaultValue;
            return self();
        }

        public B onChanged(Consumer<T> onChanged) {
            this.onChanged = onChanged;
            return self();
        }

        public B onModuleActivated(Consumer<Setting<T>> onModuleActivated) {
            this.onModuleActivated = onModuleActivated;
            return self();
        }

        public B visible(IVisible visible) {
            this.visible = visible;
            return self();
        }

        public abstract S build();
    }
}
