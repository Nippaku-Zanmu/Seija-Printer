/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.block_fixer.fixers;

import com.kijinseija.seija_printer.print_main.printer.block_fixer.AbstractFixer;
import com.kijinseija.seija_printer.print_main.printer.util.BlockUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
import com.kijinseija.seija_printer.print_main.printer.util.InvUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class DirtFixer extends AbstractFixer {
    public DirtFixer() {
        super("DirtFix");
    }

    @Override
    public int fixBlock(BlockPos pos, BlockState needState) {

        DirData dirData = new DirData(pos, BlockUtil.getSortedDirs(pos,true));

        dirData.dirs().remove(Direction.DOWN);
        for (Direction dir : dirData.dirs()) {
            for (Vec3 clickVec : dirData.clickVecsInte(dir)) {
                if (InvUtil.switchItem(stack -> needState.getBlock().equals(Blocks.DIRT_PATH)
                    ? stack.is(ItemTags.SHOVELS) : stack.is(ItemTags.HOES))) {
                    BlockUtil.interactBlock(PlaceData.newInstance(dirData.placePos(), dir, clickVec, true, null));
                    return AbstractFixer.SUCCESS;
                } else return AbstractFixer.RETURN;
            }
        }
        return AbstractFixer.CONTINUE;
    }

    @Override
    public boolean needFix(BlockPos pos, BlockState needState) {
        Block block = mc.level.getBlockState(pos).getBlock();
        if (needState.getBlock().equals(Blocks.DIRT_PATH) && !InvUtil.findItem(stack -> stack.is(ItemTags.SHOVELS)))
            return false;
        if (needState.getBlock() instanceof FarmlandBlock && !InvUtil.findItem(stack -> stack.is(ItemTags.HOES)))
            return false;
        return ((needState.getBlock().equals(Blocks.DIRT_PATH) || needState.getBlock() instanceof FarmlandBlock)
            && (block instanceof SpreadingSnowyBlock || block.equals(Blocks.DIRT)
            || block.equals(Blocks.ROOTED_DIRT) || block.equals(Blocks.PODZOL)));
    }
}
