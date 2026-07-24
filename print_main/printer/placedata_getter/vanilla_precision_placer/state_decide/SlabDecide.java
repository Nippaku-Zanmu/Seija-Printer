/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer.state_decide;

import com.kijinseija.seija_printer.settings.core.Setting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;

public class SlabDecide implements Decide {
    @Override
    public boolean isSuit(BlockState needState, BlockState nowState, BlockPos placePos) {
        return needState.getBlock().equals(nowState.getBlock()) && needState.getBlock() instanceof SlabBlock;
    }

    @Override
    public boolean test(BlockState needState, BlockState nowState, BlockPos placePos) {
        if (needState.getValue(BlockStateProperties.SLAB_TYPE)== SlabType.DOUBLE){
            return true;
        }else {
            return needState.getValue(BlockStateProperties.SLAB_TYPE) == nowState.getValue(BlockStateProperties.SLAB_TYPE);
        }
    }
    @Override
    public Setting[] getSettings() {
        return new Setting[0];
    }
}
