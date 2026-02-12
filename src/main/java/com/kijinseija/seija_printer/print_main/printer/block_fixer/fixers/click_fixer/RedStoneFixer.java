/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.block_fixer.fixers.click_fixer;

import static net.minecraft.world.level.block.RedStoneWireBlock.*;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;

public class RedStoneFixer extends AbstractClickFixer{
    public RedStoneFixer() {
        super("RedStondFix");
    }

    @Override
    public boolean needFix(BlockPos pos, BlockState needState) {
        if (!super.needFix(pos,needState)) {
            return false;
        }
        BlockState blockState = mc.level.getBlockState(pos);
        if (
            blockState.getBlock() instanceof RedStoneWireBlock &&needState.getBlock()==blockState.getBlock()) {
            return (isFullyConnected(blockState)^isFullyConnected(needState))
                &&(isNotConnected(blockState)^isNotConnected(needState))
                &&(isNotConnected(needState)||isFullyConnected(needState))
//                &&(!Blocks.REDSTONE_WIRE.getPlacementState(FakePlacementContext.getInstancePlac(Vec3d.ZERO,pos, Direction.DOWN,mc.player.getMainHandStack()))
//                .equals(blockState))
                ;
        }
        return false;
    }
    private static boolean isFullyConnected(BlockState state) {
        return state.getValue(NORTH).isConnected() && state.getValue(SOUTH).isConnected() && state.getValue(EAST).isConnected() && state.getValue(WEST).isConnected();
    }

    private static boolean isNotConnected(BlockState state) {
        return !state.getValue(NORTH).isConnected() && !state.getValue(SOUTH).isConnected() && !state.getValue(EAST).isConnected() && !state.getValue(WEST).isConnected();
    }
}
