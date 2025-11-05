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
import net.minecraft.block.*;
import net.minecraft.item.ShovelItem;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class CampFireFixer extends AbstractFixer {
    public CampFireFixer() {
        super("CampFireFix");
    }

    @Override
    public int fixBlock(BlockPos pos, BlockState needState) {
        List<Direction> interactDir = BlockUtil.getSortedDirs(pos,true);
        DirData dirData = new DirData(pos, interactDir);
        if (!InvUtil.findItem(stack -> stack.getItem()instanceof ShovelItem)) {
            return CONTINUE;
        }
        for (Direction dir : dirData.dirs()) {
            for (Vec3d clickVec : dirData.clickVecsInte(dir)) {
                if (InvUtil.switchItem(stack -> stack.getItem()instanceof ShovelItem)) {
                    BlockUtil.interactBlock(PlaceData.newInstance(dirData.placePos(),dir,clickVec,true,null));
                    return SUCCESS;
                }else return RETURN;
            }
        }
        return CONTINUE;
    }

    @Override
    public boolean needFix(BlockPos pos, BlockState needState) {
        Block block = mc.world.getBlockState(pos).getBlock();
        return needState.getBlock() instanceof CampfireBlock
            && needState.getBlock()==block
            && mc.world.getBlockState(pos).get(Properties.LIT);
    }
}
