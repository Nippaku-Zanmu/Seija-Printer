/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.settings.impl;

import meteordevelopment.meteorclient.settings.IVisible;
import meteordevelopment.meteorclient.settings.Setting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import java.util.*;
import java.util.function.Consumer;

public class BlockReplaceSetting extends Setting<HashMap<List<Block>, List<Block>>> {


    private BlockReplaceSetting(String name, String description, HashMap<List<Block>, List<Block>> defaultValue, Consumer<HashMap<List<Block>, List<Block>>> onChanged, Consumer<Setting<HashMap<List<Block>, List<Block>>>> onModuleActivated, IVisible visible) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    }

    @Override
    protected void resetImpl() {
        value = new LinkedHashMap<>(defaultValue);
    }

    @Override
    protected HashMap<List<Block>, List<Block>> parseImpl(String str) {
        return new LinkedHashMap<>(0);
    }

    @Override
    protected boolean isValueValid(HashMap<List<Block>, List<Block>> value) {
        return true;
    }


    @Override
    protected CompoundTag save(CompoundTag tag) {
        ListTag valueTag = new ListTag();//总列表 存储键值对
        for (Map.Entry<List<Block>, List<Block>> blockEntry : get().entrySet()) {
            List<Block> keyBlocks = blockEntry.getKey();
            List<Block> valueBlocks = blockEntry.getValue();

            if (valueBlocks == null)
                continue;

            //单个
            CompoundTag entryTag = new CompoundTag();//单个键值对标签

            ListTag keyBlockList = new ListTag();
            for (Block block : keyBlocks) {
                keyBlockList.add(StringTag.valueOf(BuiltInRegistries.BLOCK.getKey(block).toString()));
            }//填写key
            entryTag.put("keyBlocks", keyBlockList);

            ListTag valueBlockList = new ListTag();
            for (Block block : valueBlocks) {
                valueBlockList.add(StringTag.valueOf(BuiltInRegistries.BLOCK.getKey(block).toString()));
            }//填valueBlock
            entryTag.put("valBlocks", valueBlockList);

            valueTag.add(entryTag);

        }
        tag.put("value", valueTag);


        return tag;
    }

    @Override
    protected HashMap<List<Block>, List<Block>> load(CompoundTag tag) {
        get().clear();
        ListTag entryListTag = tag.getListOrEmpty("value");

        for (int i = 0; i < entryListTag.size(); i++) {
            CompoundTag entryTag = entryListTag.getCompound(i).orElse(null);
            if (entryTag==null)continue;
            ListTag keyBlocksTag = entryTag.getListOrEmpty("keyBlocks");
            ArrayList<Block> keyList = new ArrayList<>();
            for (Tag tagI : keyBlocksTag) {
                Block block = BuiltInRegistries.BLOCK.getValue(Identifier.parse(tagI.asString().orElse("")));
                keyList.add(block);
            }
            ListTag valBlocksTag = entryTag.getListOrEmpty("valBlocks");
            ArrayList<Block> valList = new ArrayList<>();
            for (Tag tagI : valBlocksTag) {
                Block block = BuiltInRegistries.BLOCK.getValue(Identifier.parse(tagI.asString().orElse("")));
                valList.add(block);
            }
            get().put(keyList, valList);
        }

        return get();
    }

    public static class Builder extends SettingBuilder<BlockReplaceSetting.Builder, HashMap<List<Block>, List<Block>>, BlockReplaceSetting> {
        public Builder() {
            super(new LinkedHashMap<>());
        }

        @Override
        public BlockReplaceSetting build() {
            return new BlockReplaceSetting(name, description, new LinkedHashMap<>(defaultValue), onChanged, onModuleActivated, visible);
        }


    }
}
