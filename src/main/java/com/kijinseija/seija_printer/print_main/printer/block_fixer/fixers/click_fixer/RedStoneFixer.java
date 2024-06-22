package com.kijinseija.seija_printer.print_main.printer.block_fixer.fixers.click_fixer;

import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.block.RedstoneWireBlock.*;

public class RedStoneFixer extends AbstractClickFixer{
    @Override
    public boolean needFix(BlockPos pos, BlockState needState) {
        if (!super.needFix(pos,needState)) {
            return false;
        }
        BlockState blockState = mc.world.getBlockState(pos);
        if (
            blockState.getBlock() instanceof RedstoneWireBlock &&needState.getBlock()==blockState.getBlock()) {
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
        return state.get(WIRE_CONNECTION_NORTH).isConnected() && state.get(WIRE_CONNECTION_SOUTH).isConnected() && state.get(WIRE_CONNECTION_EAST).isConnected() && state.get(WIRE_CONNECTION_WEST).isConnected();
    }

    private static boolean isNotConnected(BlockState state) {
        return !state.get(WIRE_CONNECTION_NORTH).isConnected() && !state.get(WIRE_CONNECTION_SOUTH).isConnected() && !state.get(WIRE_CONNECTION_EAST).isConnected() && !state.get(WIRE_CONNECTION_WEST).isConnected();
    }
}
