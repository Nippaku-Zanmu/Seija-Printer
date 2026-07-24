/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.settings.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kijinseija.seija_printer.settings.core.IVisible;
import com.kijinseija.seija_printer.settings.core.Setting;
import com.kijinseija.seija_printer.settings.core.Settings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import java.util.function.Consumer;

public class SettingsSetting extends Setting<Settings> {


    public SettingsSetting(String name, String description, Settings defaultValue, Consumer<Settings> onChanged, Consumer<Setting<Settings>> onModuleActivated, IVisible visible) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    }

    @Override
    protected Settings parseImpl(String str) {
        String[] split = str.split(" ");
        if (split.length == 2) {
            Setting<?> setting = value.get(split[0]);
            if (setting == null || !setting.parse(split[1])) return null;
        }
        return value;
    }

    @Override
    protected boolean isValueValid(Settings value) {
        return true;
    }

    @Override
    protected void resetImpl() {
        value.reset();
    }
//    @Override
//    public NbtCompound toTag() {
//        Addon.LOG.info("SaveTag1232");
//        NbtCompound tag = new NbtCompound();
//
//        tag.putString("name", name);
//        save(tag);
//
//        return tag;
//    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.put("settings", value.toTag());
        return tag;
    }

    @Override
    public Settings load(CompoundTag tag) {
        Tag settingsTag = tag.get("settings");
        Settings settings = value;
        if (settingsTag instanceof CompoundTag) {
            settings.fromTag((CompoundTag) settingsTag);
        }
        return settings;
    }

    @Override
    public JsonElement toJson() {
        return value.toJson();
    }

    @Override
    public boolean fromJson(JsonElement json) {
        if (json == null || !json.isJsonObject()) return false;
        value.fromJson((JsonObject) json);
        return true;
    }

    @Override
    public boolean wasChanged() {
        return true;
    }


    public static class Builder extends SettingBuilder<Builder, Settings, SettingsSetting> {

        public Builder() {
            super(new Settings());
        }

        @Override
        public Builder defaultValue(Settings s) {
            this.defaultValue = s;
            return this;
        }


        @Override
        public SettingsSetting build() {
            return new SettingsSetting(name, description, defaultValue, onChanged, onModuleActivated, visible);
        }
    }
}
