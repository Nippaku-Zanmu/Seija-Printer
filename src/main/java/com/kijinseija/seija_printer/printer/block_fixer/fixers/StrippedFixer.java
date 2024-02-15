package com.kijinseija.seija_printer.printer.block_fixer.fixers;

import com.kijinseija.seija_printer.printer.block_fixer.AbstractFixer;
import com.kijinseija.seija_printer.printer.util.BlockUtil;
import com.kijinseija.seija_printer.printer.util.InvUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.AxeItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;

public class StrippedFixer extends AbstractFixer {
    @Override
    public boolean fixBlock(BlockPos pos, BlockState needState) {
        List<Direction> interactDir = BlockUtil.getInteractDir(pos);
        for (Direction dir : interactDir) {
            if (InvUtil.switchItem(stack -> stack.getItem() instanceof AxeItem)) {
                BlockUtil.interactBlock(pos, dir);
                return true;
            }

        }
        return false;
    }

    @Override
    public boolean needFix(BlockPos pos, BlockState needState) {
        BlockState blockState = mc.world.getBlockState(pos);
        //不用内置规则替换
        Block needBlock =needState.getBlock();
        Block strippedBlock = AxeItem.STRIPPED_BLOCKS.get(blockState.getBlock());
        return needBlock.equals(strippedBlock);

    }
}
