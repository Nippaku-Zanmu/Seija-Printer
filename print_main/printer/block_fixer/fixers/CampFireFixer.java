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
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
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
            for (Vec3 clickVec : dirData.clickVecsInte(dir)) {
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
        Block block = mc.level.getBlockState(pos).getBlock();
        return needState.getBlock() instanceof CampfireBlock
            && needState.getBlock()==block
            && mc.level.getBlockState(pos).getValue(BlockStateProperties.LIT);
    }
}
