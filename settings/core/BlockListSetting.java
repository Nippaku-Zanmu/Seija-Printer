/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.settings.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class BlockListSetting extends Setting<List<Block>> {
    protected BlockListSetting(
        String name,
        String description,
        List<Block> defaultValue,
        Consumer<List<Block>> onChanged,
        Consumer<Setting<List<Block>>> onModuleActivated,
        IVisible visible
    ) {
        super(name, description, copy(defaultValue), onChanged, onModuleActivated, visible);
        value = copy(defaultValue);
    }

    private static List<Block> copy(List<Block> blocks) {
        return blocks == null ? new ArrayList<>() : new ArrayList<>(blocks);
    }

    @Override
    protected List<Block> parseImpl(String text) {
        List<Block> blocks = new ArrayList<>();
        if (text == null || text.isBlank()) return blocks;

        for (String value : text.split(",")) {
            try {
                blocks.add(BuiltInRegistries.BLOCK.getValue(Identifier.parse(value.trim())));
            } catch (RuntimeException ignored) {
                return null;
            }
        }
        return blocks;
    }

    @Override
    protected boolean isValueValid(List<Block> value) {
        return value != null && value.stream().noneMatch(java.util.Objects::isNull);
    }

    @Override
    protected void resetImpl() {
        value = copy(defaultValue);
    }

    @Override
    protected CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (Block block : value) list.add(StringTag.valueOf(BuiltInRegistries.BLOCK.getKey(block).toString()));
        tag.put("value", list);
        return tag;
    }

    @Override
    protected List<Block> load(CompoundTag tag) {
        List<Block> blocks = new ArrayList<>();
        for (Tag valueTag : tag.getListOrEmpty("value")) {
            String id = valueTag.asString().orElse("");
            if (!id.isEmpty()) blocks.add(BuiltInRegistries.BLOCK.getValue(Identifier.parse(id)));
        }
        return blocks;
    }

    @Override
    public JsonElement toJson() {
        JsonArray json = new JsonArray();
        for (Block block : value) json.add(new JsonPrimitive(BuiltInRegistries.BLOCK.getKey(block).toString()));
        return json;
    }

    @Override
    public boolean fromJson(JsonElement json) {
        if (json == null || !json.isJsonArray()) return false;
        List<Block> blocks = new ArrayList<>();
        try {
            for (JsonElement element : json.getAsJsonArray()) {
                blocks.add(BuiltInRegistries.BLOCK.getValue(Identifier.parse(element.getAsString())));
            }
        } catch (RuntimeException ignored) {
            return false;
        }
        return set(blocks);
    }

    public static class Builder extends SettingBuilder<Builder, List<Block>, BlockListSetting> {
        public Builder() {
            super(new ArrayList<>());
        }

        @Override
        public Builder defaultValue(List<Block> blocks) {
            defaultValue = copy(blocks);
            return this;
        }

        public Builder defaultValue(Block... blocks) {
            return defaultValue(blocks == null ? List.of() : Arrays.asList(blocks));
        }

        @Override
        public BlockListSetting build() {
            return new BlockListSetting(name, description, defaultValue, onChanged, onModuleActivated, visible);
        }
    }
}
