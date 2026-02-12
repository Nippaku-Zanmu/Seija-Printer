/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.settings.impl;

import com.kijinseija.seija_printer.Addon;
import meteordevelopment.meteorclient.settings.IVisible;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.Settings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import java.util.function.Consumer;

public class SettingsSetting extends Setting<Settings> {


    public SettingsSetting(String name, String description, Settings defaultValue, Consumer<Settings> onChanged, Consumer<Setting<Settings>> onModuleActivated, IVisible visible) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    }

    @Override
    protected Settings parseImpl(String str) {
        Addon.LOG.info("aadadwad");
        String[] split = str.split(" ");
        if (split.length == 2) {
            Setting<?> setting = value.get(split[0]);
            setting.parse(split[1]);
        }
        return value;
    }

    @Override
    protected boolean isValueValid(Settings value) {
        return true;
    }

    @Override
    protected void resetImpl() {
        super.resetImpl();
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
        Settings settings = defaultValue;
        Addon.LOG.info("Def" + defaultValue.toTag().asString());
        if (settingsTag instanceof CompoundTag) {
            settings.fromTag((CompoundTag) settingsTag);
        }
        return settings;
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
