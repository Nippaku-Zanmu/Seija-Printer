/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer.state_decide;

import com.kijinseija.seija_printer.settings.core.BoolSetting;
import com.kijinseija.seija_printer.settings.core.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FallingBlockDecide implements Decide {
    private static final Minecraft mc = Minecraft.getInstance();
    private final Setting<Boolean> isEnable = new BoolSetting.Builder()
        .name("avoid-block-fall")
        .defaultValue(true)
        .build();

    @Override
    public boolean isSuit(BlockState needState, BlockState nowState, BlockPos placePos) {
        return isEnable.get() && needState.getBlock().equals(nowState.getBlock()) && needState.getBlock() instanceof FallingBlock;
    }

    @Override
    public boolean test(BlockState needState, BlockState nowState, BlockPos placePos) {
        return mc.level != null && !FallingBlock.isFree(mc.level.getBlockState(placePos.below()))
            && MainDecide.defaultTest(needState, nowState, placePos);
    }

    @Override
    public Setting<?>[] getSettings() {
        return new Setting[]{isEnable};
    }
}
