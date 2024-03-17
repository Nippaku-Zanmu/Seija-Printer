package com.kijinseija.seija_printer.print_main.printer.block_fixer.fixers.click_fixer;

import net.minecraft.block.BlockState;
import net.minecraft.block.DaylightDetectorBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;

public class DaylightDetectorFixer extends AbstractClickFixer{
    @Override
    public boolean needFix(BlockPos pos, BlockState needState) {
        BlockState blockState = mc.world.getBlockState(pos);
        if (
            blockState.getBlock() instanceof DaylightDetectorBlock &&needState.getBlock()==blockState.getBlock()) {
            return! blockState.get(Properties.INVERTED) .equals (needState.get(Properties.INVERTED));
        }
        return false;
    }
}
