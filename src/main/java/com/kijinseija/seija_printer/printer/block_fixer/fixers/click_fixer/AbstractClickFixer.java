package com.kijinseija.seija_printer.printer.block_fixer.fixers.click_fixer;

import com.kijinseija.seija_printer.printer.block_fixer.AbstractFixer;
import com.kijinseija.seija_printer.printer.util.BlockUtil;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;

public abstract class AbstractClickFixer extends AbstractFixer {
    @Override
    public boolean fixBlock(BlockPos pos, BlockState needState) {
        List<Direction> interactDir = BlockUtil.getInteractDir(pos);
        if (interactDir.isEmpty())
            return false;
        BlockUtil.interactBlock(pos,interactDir.get(0));
        return true;
    }
}
