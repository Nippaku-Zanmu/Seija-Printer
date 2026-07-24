/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.modules;

import com.kijinseija.seija_printer.print_main.printer.util.BlockReplaceUtils;
import com.kijinseija.seija_printer.settings.core.BlockPosSetting;
import com.kijinseija.seija_printer.settings.core.Setting;
import com.kijinseija.seija_printer.settings.core.SettingGroup;
import com.kijinseija.seija_printer.utils.player.ChatUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class ScheDebug extends ClientModule {
    public ScheDebug() {
        super("ScheDebug", "");
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
