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
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class StrippedFixer extends AbstractFixer {
    public StrippedFixer() {
        super("LogFix");
    }

    @Override
    public int fixBlock(BlockPos pos, BlockState needState) {
        List<Direction> interactDir = BlockUtil.getSortedDirs(pos,true);
        DirData dirData = new DirData(pos, interactDir);
        if (InvUtil.findItem(stack -> stack.getItem() instanceof AxeItem)) {
            for (Direction dir : interactDir) {
                for (Vec3 clickVec : dirData.clickVecsInte(dir)) {
                    if (InvUtil.switchItem(stack -> stack.getItem() instanceof AxeItem)) {
                        BlockUtil.interactBlock(PlaceData.newInstance(dirData.placePos(),dir,clickVec,true,null));
                        return SUCCESS;
                    }else return RETURN;
                }
            }
        }
        return CONTINUE;
    }

    @Override
    public boolean needFix(BlockPos pos, BlockState needState) {
        BlockState blockState = mc.level.getBlockState(pos);
        //不用内置规则替换
        Block needBlock = needState.getBlock();
        Block strippedBlock = AxeItem.STRIPPABLES.get(blockState.getBlock());
        return needBlock.equals(strippedBlock);

    }
}
