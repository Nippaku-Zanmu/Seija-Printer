/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.settings.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Settings {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public final List<SettingGroup> groups = new ArrayList<>();
    private final SettingGroup defaultGroup;

    public Settings() {
        defaultGroup = createGroup("General");
    }

    public SettingGroup getDefaultGroup() {
        return defaultGroup;
    }

    public SettingGroup createGroup(String name) {
        return createGroup(name, true);
    }

    public SettingGroup createGroup(String name, boolean expanded) {
        SettingGroup group = new SettingGroup(name, expanded);
        groups.add(group);
        return group;
    }

    public List<SettingGroup> values() {
        return Collections.unmodifiableList(groups);
    }

    public Setting<?> get(String name) {
        for (SettingGroup group : groups) {
            Setting<?> setting = group.get(name);
            if (setting != null) return setting;
        }
        return null;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        for (SettingGroup group : groups) tag.put(group.name, group.toTag());
        return tag;
    }

    public void fromTag(CompoundTag tag) {
        if (tag == null) return;
        for (SettingGroup group : groups) {
            Tag groupTag = tag.get(group.name);
            if (groupTag instanceof CompoundTag compoundTag) group.fromTag(compoundTag);
        }
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        for (SettingGroup group : groups) json.add(group.name, group.toJson());
        return json;
    }

    public String toJsonString() {
        return GSON.toJson(toJson());
    }

    public void fromJson(JsonObject json) {
        if (json == null) return;
        for (SettingGroup group : groups) {
            JsonElement groupJson = json.get(group.name);
            if (groupJson != null && groupJson.isJsonObject()) group.fromJson(groupJson.getAsJsonObject());
        }
    }

    public boolean fromJsonString(String json) {
        try {
            JsonElement element = JsonParser.parseString(json);
            if (!element.isJsonObject()) return false;
            fromJson(element.getAsJsonObject());
            return true;
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    public void reset() {
        groups.forEach(SettingGroup::reset);
    }

    public void onModuleActivated() {
        for (SettingGroup group : groups) {
            for (Setting<?> setting : group.settings) setting.onModuleActivated();
        }
    }
}
