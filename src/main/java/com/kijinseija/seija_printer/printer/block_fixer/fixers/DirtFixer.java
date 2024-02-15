package com.kijinseija.seija_printer.printer.block_fixer.fixers;

import com.kijinseija.seija_printer.printer.block_fixer.AbstractFixer;
import com.kijinseija.seija_printer.printer.util.BlockUtil;
import com.kijinseija.seija_printer.printer.util.InvUtil;
import net.minecraft.block.*;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;

public class DirtFixer extends AbstractFixer {
    @Override
    public boolean fixBlock(BlockPos pos, BlockState needState) {
        List<Direction> interactDir = BlockUtil.getInteractDir(pos);
        if (needState.getBlock() instanceof DirtPathBlock)
            interactDir.remove(Direction.DOWN);
        for (Direction dir : interactDir) {
            if (InvUtil.switchItem(stack -> needState.getBlock() instanceof DirtPathBlock
                ? stack.getItem() instanceof ShovelItem : stack.getItem() instanceof HoeItem)) {
                BlockUtil.interactBlock(pos, dir);
                return true;
            }

        }
        return false;
    }

    @Override
    public boolean needFix(BlockPos pos, BlockState needState) {
        Block block = mc.world.getBlockState(pos).getBlock();
        return ((needState.getBlock() instanceof DirtPathBlock || needState.getBlock() instanceof FarmlandBlock)
            && (block instanceof SpreadableBlock || block.equals(Blocks.DIRT)
            || block.equals(Blocks.ROOTED_DIRT) || block.equals(Blocks.PODZOL)));
    }
}
