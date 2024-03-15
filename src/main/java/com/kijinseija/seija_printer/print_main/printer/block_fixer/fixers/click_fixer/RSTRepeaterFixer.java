package com.kijinseija.seija_printer.print_main.printer.block_fixer.fixers.click_fixer;

import net.minecraft.block.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;

public class RSTRepeaterFixer extends AbstractClickFixer {


    @Override
    public boolean needFix(BlockPos pos, BlockState needState) {
        BlockState blockState = mc.world.getBlockState(pos);
        if (
            blockState.getBlock() instanceof RepeaterBlock
                &&needState.getBlock()==blockState.getBlock()) {
            return !blockState.get(Properties.DELAY).equals(needState.get(Properties.DELAY));
        }
        return false;
    }
}
