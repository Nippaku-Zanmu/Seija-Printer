/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.block_fixer.fixers.click_fixer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class RSTRepeaterFixer extends AbstractClickFixer {


    public RSTRepeaterFixer() {
        super("RepeaterFix");
    }

    @Override
    public boolean needFix(BlockPos pos, BlockState needState) {
        if (!super.needFix(pos,needState)) {
            return false;
        }
        BlockState blockState = mc.level.getBlockState(pos);
        if (
            blockState.getBlock() instanceof RepeaterBlock
                &&needState.getBlock()==blockState.getBlock()) {
            return !blockState.getValue(BlockStateProperties.DELAY).equals(needState.getValue(BlockStateProperties.DELAY));
        }
        return false;
    }
}
