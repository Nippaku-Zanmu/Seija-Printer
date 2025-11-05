/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.settings.impl;

import meteordevelopment.meteorclient.settings.IVisible;
import meteordevelopment.meteorclient.settings.Setting;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class DirectionListSetting extends Setting<List<Direction>> {
    public DirectionListSetting(String name, String description, List<Direction> defaultValue, Consumer<List<Direction>> onChanged, Consumer<Setting<List<Direction>>> onModuleActivated, IVisible visible) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    }

    @Override
    protected List<Direction> parseImpl(String str) {
        String[] values = str.split(",");
        List<Direction> dirs = new ArrayList<>(values.length);
        for (String s : values) {
            Direction dir = Direction.byId(s);
            if (dir != null) dirs.add(dir);
        }
        return dirs;
    }

    @Override
    protected boolean isValueValid(List<Direction> value) {
        return true;
    }

    @Override
    protected void resetImpl() {
        value = new ArrayList<>();
    }

    @Override
    public NbtCompound save(NbtCompound tag) {
        NbtList valueTag = new NbtList();
        for (Direction dir : get()) {
            valueTag.add(NbtString.of(dir.getId()));
        }
        tag.put("value", valueTag);

        return tag;
    }

    @Override
    public List<Direction> load(NbtCompound tag) {
        get().clear();

        NbtList valueTag = tag.getListOrEmpty("value");
        for (NbtElement tagI : valueTag) {
            Direction dir = Direction.byId(tagI.asString().orElse(null));
            if (dir != null)
                get().add(dir);
        }

        return get();
    }

    public static class Builder extends SettingBuilder<Builder, List<Direction>, DirectionListSetting> {

        public Builder() {
            super(new ArrayList<>(0));
        }

        @Override
        public Builder defaultValue(List<Direction> map) {
            this.defaultValue = map;
            return this;
        }

        public DirectionListSetting.Builder defaultValue(Direction... defaults) {
            return defaultValue(defaults != null ? Arrays.asList(defaults) : new ArrayList<>());
        }

        @Override
        public DirectionListSetting build() {
            return new DirectionListSetting(name, description, defaultValue, onChanged, onModuleActivated, visible);
        }
    }
}
