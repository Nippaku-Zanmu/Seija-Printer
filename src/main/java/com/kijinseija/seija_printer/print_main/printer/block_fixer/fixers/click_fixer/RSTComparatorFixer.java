/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.block_fixer.fixers.click_fixer;

import net.minecraft.block.BlockState;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;

public class RSTComparatorFixer extends AbstractClickFixer{
    public RSTComparatorFixer() {
        super("ComparatorFix");
    }

    @Override
    public boolean needFix(BlockPos pos, BlockState needState) {
        if (!super.needFix(pos,needState)) {
            return false;
        }
        BlockState blockState = mc.world.getBlockState(pos);
        if (
            blockState.getBlock() instanceof ComparatorBlock&&needState.getBlock()==blockState.getBlock()) {
            return blockState.get(Properties.COMPARATOR_MODE) != needState.get(Properties.COMPARATOR_MODE);
        }
        return false;
    }
}
