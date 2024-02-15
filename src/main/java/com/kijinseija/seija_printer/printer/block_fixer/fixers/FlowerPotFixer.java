package com.kijinseija.seija_printer.printer.block_fixer.fixers;

import com.kijinseija.seija_printer.printer.block_fixer.AbstractFixer;
import com.kijinseija.seija_printer.printer.util.BlockUtil;
import com.kijinseija.seija_printer.printer.util.InvUtil;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;

public class FlowerPotFixer extends AbstractFixer {
    @Override
    public boolean fixBlock(BlockPos pos, BlockState needState) {
        List<Direction> interactDir = BlockUtil.getInteractDir(pos);
        //block替换 仅自定义替换,不使用内置替换
        if ((!interactDir.isEmpty())&&needState.getBlock() instanceof FlowerPotBlock){
            if (InvUtil.switchBlock(((FlowerPotBlock) needState.getBlock()).getContent())) {

                BlockUtil.interactBlock(pos,interactDir.get(0));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean needFix(BlockPos pos, BlockState needState) {

        BlockState blockState = mc.world.getBlockState(pos);
        return blockState.getBlock() instanceof FlowerPotBlock
            //仅自定义替换,不使用内置替换规则
            && needState.getBlock() instanceof FlowerPotBlock
            && ((FlowerPotBlock) blockState.getBlock()).getContent() instanceof AirBlock
            && (!(((FlowerPotBlock) needState.getBlock()).getContent() instanceof AirBlock));
    }
}
