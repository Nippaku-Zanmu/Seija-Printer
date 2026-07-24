/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.settings.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Consumer;

public class BlockPosSetting extends Setting<BlockPos> {
    protected BlockPosSetting(
        String name,
        String description,
        BlockPos defaultValue,
        Consumer<BlockPos> onChanged,
        Consumer<Setting<BlockPos>> onModuleActivated,
        IVisible visible
    ) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    }

    @Override
    protected BlockPos parseImpl(String text) {
        if (text == null) return null;
        String[] parts = text.trim().split("[,\\s]+", 3);
        if (parts.length != 3) return null;

        try {
            return new BlockPos(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    @Override
    protected boolean isValueValid(BlockPos value) {
        return true;
    }

    @Override
    protected CompoundTag save(CompoundTag tag) {
        if (value != null) {
            tag.putInt("x", value.getX());
            tag.putInt("y", value.getY());
            tag.putInt("z", value.getZ());
        }
        return tag;
    }

    @Override
    protected BlockPos load(CompoundTag tag) {
        if (!tag.contains("x") || !tag.contains("y") || !tag.contains("z")) return defaultValue;
        return new BlockPos(
            tag.getInt("x").orElse(0),
            tag.getInt("y").orElse(0),
            tag.getInt("z").orElse(0)
        );
    }

    @Override
    public JsonElement toJson() {
        if (value == null) return com.google.gson.JsonNull.INSTANCE;
        JsonObject json = new JsonObject();
        json.add("x", new JsonPrimitive(value.getX()));
        json.add("y", new JsonPrimitive(value.getY()));
        json.add("z", new JsonPrimitive(value.getZ()));
        return json;
    }

    @Override
    public boolean fromJson(JsonElement json) {
        if (json == null || !json.isJsonObject()) return false;
        JsonObject object = json.getAsJsonObject();
        if (!object.has("x") || !object.has("y") || !object.has("z")) return false;
        return set(new BlockPos(object.get("x").getAsInt(), object.get("y").getAsInt(), object.get("z").getAsInt()));
    }

    public static class Builder extends SettingBuilder<Builder, BlockPos, BlockPosSetting> {
        public Builder() {
            super(null);
        }

        @Override
        public BlockPosSetting build() {
            return new BlockPosSetting(name, description, defaultValue, onChanged, onModuleActivated, visible);
        }
    }
}
