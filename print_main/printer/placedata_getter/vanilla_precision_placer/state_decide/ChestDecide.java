/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer.state_decide;

import com.kijinseija.seija_printer.settings.core.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;

public class ChestDecide implements Decide{
    private static final  Minecraft mc = Minecraft.getInstance();
    @Override
    public boolean isSuit(BlockState needState, BlockState nowState, BlockPos placePos) {
        return needState.getBlock().equals(nowState.getBlock())&&(
            needState.getBlock() instanceof ChestBlock);
    }

    @Override
    public boolean test(BlockState needState, BlockState nowState, BlockPos placePos) {
        if (!needState.getValue(BlockStateProperties.HORIZONTAL_FACING).equals(nowState.getValue(BlockStateProperties.HORIZONTAL_FACING)))
        {
            return false;
        }//方位判断
        ChestType needType = needState.getValue(BlockStateProperties.CHEST_TYPE);
        ChestType nowType = nowState.getValue(BlockStateProperties.CHEST_TYPE);
        if ((!needType.equals(nowType))&&nowType.equals(ChestType.SINGLE)){
            //特殊情况 旁边的箱子可能没放
            Direction chestDir = nowState.getValue(BlockStateProperties.HORIZONTAL_FACING);
            Direction neiOffset = getNeiOffset(chestDir, needType);
            BlockPos neiPos = placePos.relative(neiOffset);
            //基本数据
            BlockState neiState = mc.level.getBlockState(neiPos);
            //旁边不是箱子或者旁边是箱子但是方向错误
            return !neiState.getBlock().equals(needState.getBlock())
                || !neiState.getValue(BlockStateProperties.HORIZONTAL_FACING).equals(chestDir);

        }
        return needType.equals(nowType);
    }
    public Direction getNeiOffset(Direction cDir, ChestType t) {
        switch (t) {
            case LEFT -> {
                return cDir.getClockWise();
            }
            case RIGHT -> {
                return cDir.getCounterClockWise();
            }
        }
        return null;
    }
    @Override
    public Setting[] getSettings() {
        return new Setting[0];
    }
}
