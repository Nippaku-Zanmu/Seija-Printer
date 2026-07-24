/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.block_fixer.fixers.click_fixer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class DoorFixer extends AbstractClickFixer {
    public DoorFixer() {
        super("DoorFix");
    }

    @Override
    public boolean needFix(BlockPos pos, BlockState needState) {
        if (!super.needFix(pos,needState)) {
            return false;
        }
        BlockState blockState = mc.level.getBlockState(pos);
        if (blockState.getBlock()!= Blocks.IRON_DOOR
            &&blockState.getBlock()!= Blocks.IRON_TRAPDOOR
            &&(blockState.getBlock() instanceof DoorBlock
            || blockState.getBlock() instanceof FenceGateBlock
            || blockState.getBlock() instanceof TrapDoorBlock)) {
            return needState.getBlock().getClass().equals(blockState.getBlock().getClass())
                &&blockState.getValue(BlockStateProperties.OPEN) != needState.getValue(BlockStateProperties.OPEN);
        }
        return false;
    }
}
