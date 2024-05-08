package com.kijinseija.seija_printer.print_main.printer.block_fixer.fixers.click_fixer;

import net.minecraft.block.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;

public class DoorFixer extends AbstractClickFixer {
    @Override
    public boolean needFix(BlockPos pos, BlockState needState) {
        if (!super.needFix(pos,needState)) {
            return false;
        }
        BlockState blockState = mc.world.getBlockState(pos);
        if (blockState.getBlock()!= Blocks.IRON_DOOR
            &&blockState.getBlock()!= Blocks.IRON_TRAPDOOR
            &&(blockState.getBlock() instanceof DoorBlock
            || blockState.getBlock() instanceof FenceGateBlock
            || blockState.getBlock() instanceof TrapdoorBlock)) {
            return needState.getBlock()==blockState.getBlock()
                &&blockState.get(Properties.OPEN) != needState.get(Properties.OPEN);
        }
        return false;
    }
}
