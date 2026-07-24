/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.block_fixer.fixers;

import com.kijinseija.seija_printer.print_main.printer.block_fixer.AbstractFixer;
import com.kijinseija.seija_printer.print_main.printer.util.BlockUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
import com.kijinseija.seija_printer.print_main.printer.util.records.DirDataI;
import com.kijinseija.seija_printer.print_main.printer.util.InvUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class FlowerPotFixer extends AbstractFixer {
    public FlowerPotFixer() {
        super("FlowerPotFix");
    }

    @Override
    public int fixBlock(BlockPos pos, BlockState needState) {

        //block替换 仅自定义替换,不使用内置替换
        DirData dirData = new DirData(pos, BlockUtil.getSortedDirs(pos,true));

        if (InvUtil.findBlock(((FlowerPotBlock) needState.getBlock()).getPotted())){
            for (Direction dir : dirData.dirs()) {
                for (Vec3 clickVec : dirData.clickVecsInte(dir)) {
                    if (!InvUtil.switchBlock(((FlowerPotBlock) needState.getBlock()).getPotted())) {
                        return RETURN;
                    }
                    BlockUtil.interactBlock(PlaceData.newInstance(dirData.placePos(),dir,clickVec,true,null));
                    return SUCCESS;
                }
            }
        }
        return CONTINUE;
    }

    @Override
    public boolean needFix(BlockPos pos, BlockState needState) {

        BlockState blockState = mc.level.getBlockState(pos);
        return blockState.getBlock() instanceof FlowerPotBlock
            //仅自定义替换,不使用内置替换规则
            && needState.getBlock() instanceof FlowerPotBlock
            && ((FlowerPotBlock) blockState.getBlock()).getPotted() instanceof AirBlock
            && (!(((FlowerPotBlock) needState.getBlock()).getPotted() instanceof AirBlock))
            &&((FlowerPotBlock) blockState.getBlock()).getPotted() instanceof AirBlock;
    }
}
