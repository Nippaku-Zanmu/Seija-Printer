package com.kijinseija.seija_printer.settings.impl;

import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Data editor for block replacement mappings; rendering is handled by the native screen. */
public final class BlockReplaceSettingScreen {
    private final BlockReplaceSetting setting;

    public BlockReplaceSettingScreen(BlockReplaceSetting setting) {
        this.setting = setting;
    }

    public BlockReplaceSettingScreen(Object ignoredTheme, BlockReplaceSetting setting) {
        this(setting);
    }

    public void addMapping(List<Block> source, List<Block> replacement) {
        setting.get().put(new ArrayList<>(source), new ArrayList<>(replacement));
    }

    public void removeMapping(List<Block> source) {
        setting.get().remove(source);
    }

    public Map<List<Block>, List<Block>> mappings() {
        return setting.get();
    }
}
