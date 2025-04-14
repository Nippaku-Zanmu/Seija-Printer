package com.kijinseija.seija_printer.settings.impl;

import meteordevelopment.meteorclient.settings.IVisible;
import meteordevelopment.meteorclient.settings.Setting;
import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Consumer;

public class BlockReplaceSetting extends Setting<HashMap<List<Block>, List<Block>>> {


    public BlockReplaceSetting(String name, String description, HashMap<List<Block>, List<Block>> defaultValue, Consumer<HashMap<List<Block>, List<Block>>> onChanged, Consumer<Setting<HashMap<List<Block>, List<Block>>>> onModuleActivated, IVisible visible) {
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
    protected NbtCompound save(NbtCompound tag) {
        NbtList valueTag = new NbtList();//总列表 存储键值对
        for (Map.Entry<List<Block>, List<Block>> blockEntry : get().entrySet()) {
            List<Block> keyBlocks = blockEntry.getKey();
            List<Block> valueBlocks = blockEntry.getValue();

            if (valueBlocks == null)
                continue;

            //单个
            NbtCompound entryTag = new NbtCompound();//单个键值对标签

            NbtList keyBlockList = new NbtList();
            for (Block block : keyBlocks) {
                keyBlockList.add(NbtString.of(Registries.BLOCK.getId(block).toString()));
            }//填写key
            entryTag.put("keyBlocks", keyBlockList);

            NbtList valueBlockList = new NbtList();
            for (Block block : valueBlocks) {
                valueBlockList.add(NbtString.of(Registries.BLOCK.getId(block).toString()));
            }//填valueBlock
            entryTag.put("valBlocks", valueBlockList);

            valueTag.add(entryTag);

        }
        tag.put("value", valueTag);


        return tag;
    }

    @Override
    protected HashMap<List<Block>, List<Block>> load(NbtCompound tag) {
        get().clear();
        NbtList entryListTag = tag.getListOrEmpty("value");

        for (int i = 0; i < entryListTag.size(); i++) {
            NbtCompound entryTag = entryListTag.getCompound(i).orElse(null);
            if (entryTag==null)continue;
            NbtList keyBlocksTag = entryTag.getListOrEmpty("keyBlocks");
            ArrayList<Block> keyList = new ArrayList<>();
            for (NbtElement tagI : keyBlocksTag) {
                Block block = Registries.BLOCK.get(Identifier.of(tagI.asString().orElse("")));
                keyList.add(block);
            }
            NbtList valBlocksTag = entryTag.getListOrEmpty("valBlocks");
            ArrayList<Block> valList = new ArrayList<>();
            for (NbtElement tagI : valBlocksTag) {
                Block block = Registries.BLOCK.get(Identifier.of(tagI.asString().orElse("")));
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
