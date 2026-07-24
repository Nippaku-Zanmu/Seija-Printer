/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.settings.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SettingGroup {
    public final String name;
    public final boolean expanded;
    public final List<Setting<?>> settings = new ArrayList<>();

    SettingGroup(String name, boolean expanded) {
        this.name = name == null ? "" : name;
        this.expanded = expanded;
    }

    public <S extends Setting<?>> S add(S setting) {
        if (setting == null) throw new IllegalArgumentException("setting cannot be null");
        settings.add(setting);
        return setting;
    }

    public List<Setting<?>> values() {
        return Collections.unmodifiableList(settings);
    }

    public Setting<?> get(String settingName) {
        for (Setting<?> setting : settings) {
            if (setting.name.equals(settingName)) return setting;
        }
        return null;
    }

    CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        for (Setting<?> setting : settings) tag.put(setting.name, setting.toTag());
        return tag;
    }

    void fromTag(CompoundTag tag) {
        for (Setting<?> setting : settings) {
            Tag settingTag = tag.get(setting.name);
            if (settingTag instanceof CompoundTag compoundTag) setting.fromTag(compoundTag);
        }
    }

    JsonObject toJson() {
        JsonObject json = new JsonObject();
        for (Setting<?> setting : settings) json.add(setting.name, setting.toJson());
        return json;
    }

    void fromJson(JsonObject json) {
        for (Setting<?> setting : settings) {
            JsonElement value = json.get(setting.name);
            if (value != null) setting.fromJson(value);
        }
    }

    public void reset() {
        settings.forEach(Setting::reset);
    }
}
