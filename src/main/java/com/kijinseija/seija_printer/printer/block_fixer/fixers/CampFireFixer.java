package com.kijinseija.seija_printer.printer.block_fixer.fixers;

import com.kijinseija.seija_printer.printer.block_fixer.AbstractFixer;
import com.kijinseija.seija_printer.printer.util.BlockUtil;
import com.kijinseija.seija_printer.printer.util.InvUtil;
import net.minecraft.block.*;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;

public class CampFireFixer extends AbstractFixer {
    @Override
    public boolean fixBlock(BlockPos pos, BlockState needState) {
        List<Direction> interactDir = BlockUtil.getInteractDir(pos);
        for (Direction dir : interactDir) {
            if (InvUtil.switchItem(stack -> stack.getItem()instanceof ShovelItem)) {
                BlockUtil.interactBlock(pos, dir);
                return true;
            }

        }
        return false;
    }

    @Override
    public boolean needFix(BlockPos pos, BlockState needState) {
        Block block = mc.world.getBlockState(pos).getBlock();
        return needState.getBlock() instanceof CampfireBlock
            && needState.getBlock()==block
            && mc.world.getBlockState(pos).get(Properties.LIT);
    }
}
