/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.modules;

import com.kijinseija.seija_printer.Addon;
import com.kijinseija.seija_printer.print_main.printer.util.BlockReplaceUtils;
import meteordevelopment.meteorclient.settings.BlockPosSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class ScheDebug extends Module {
    public ScheDebug() {
        super(Addon.CATEGORY, "ScheDebug", "");
    }
    SettingGroup sgDefault = settings.getDefaultGroup();
    private final Setting<BlockPos> posSetting = sgDefault.add(new BlockPosSetting.Builder()
        .name("Pos")
        .build());

    @Override
    public void onActivate() {
        ChatUtils.sendMsg(Component.nullToEmpty(BlockReplaceUtils.INSTANCE.getScheState(posSetting.get()).getBlock().toString()));
    }
}
