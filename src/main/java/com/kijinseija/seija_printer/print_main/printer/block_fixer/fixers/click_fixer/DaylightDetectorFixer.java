/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.block_fixer.fixers.click_fixer;

import net.minecraft.block.BlockState;
import net.minecraft.block.DaylightDetectorBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;

public class DaylightDetectorFixer extends AbstractClickFixer{
    public DaylightDetectorFixer() {
        super("DaylightDetectorFix");
    }

    @Override
    public boolean needFix(BlockPos pos, BlockState needState) {
        if (!super.needFix(pos,needState)) {
            return false;
        }
        BlockState blockState = mc.world.getBlockState(pos);
        if (
            blockState.getBlock() instanceof DaylightDetectorBlock &&needState.getBlock()==blockState.getBlock()) {
            return! blockState.get(Properties.INVERTED) .equals (needState.get(Properties.INVERTED));
        }
        return false;
    }
}
