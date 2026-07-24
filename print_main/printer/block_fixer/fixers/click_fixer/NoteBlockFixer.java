/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.block_fixer.fixers.click_fixer;

import com.kijinseija.seija_printer.settings.core.BoolSetting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import java.util.Objects;

public class NoteBlockFixer extends AbstractClickFixer{
    public NoteBlockFixer() {
        super(new BoolSetting.Builder().name("NoteBlockFix").defaultValue(false).build());
    }

    @Override
    public boolean needFix(BlockPos pos, BlockState needState) {
        if (!super.needFix(pos,needState)) {
            return false;
        }
        BlockState blockState = mc.level.getBlockState(pos);
        if (
            blockState.getBlock() instanceof NoteBlock &&needState.getBlock()==blockState.getBlock()) {
            return !Objects.equals(blockState.getValue(BlockStateProperties.NOTE), needState.getValue(BlockStateProperties.NOTE));
        }
        return false;
    }
}
