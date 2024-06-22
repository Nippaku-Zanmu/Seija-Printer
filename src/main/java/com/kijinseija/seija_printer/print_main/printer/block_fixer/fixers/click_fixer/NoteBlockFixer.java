package com.kijinseija.seija_printer.print_main.printer.block_fixer.fixers.click_fixer;

import net.minecraft.block.BlockState;
import net.minecraft.block.NoteBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public class NoteBlockFixer extends AbstractClickFixer{
    @Override
    public boolean needFix(BlockPos pos, BlockState needState) {
        if (!super.needFix(pos,needState)) {
            return false;
        }
        BlockState blockState = mc.world.getBlockState(pos);
        if (
            blockState.getBlock() instanceof NoteBlock &&needState.getBlock()==blockState.getBlock()) {
            return !Objects.equals(blockState.get(Properties.NOTE), needState.get(Properties.NOTE));
        }
        return false;
    }
}
