package com.kijinseija.seija_printer.print_main.printer.block_fixer.fixers;

import com.kijinseija.seija_printer.print_main.printer.block_fixer.AbstractFixer;
import com.kijinseija.seija_printer.print_main.printer.util.BlockUtil;
import com.kijinseija.seija_printer.print_main.printer.util.DirDataI;
import com.kijinseija.seija_printer.print_main.printer.util.InvUtil;
import com.kijinseija.seija_printer.print_main.printer.util.PlaceData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.AxeItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class StrippedFixer extends AbstractFixer {
    @Override
    public boolean fixBlock(BlockPos pos, BlockState needState) {
        List<Direction> interactDir = BlockUtil.getInteractDir(pos);
        DirDataI dirDataI = new DirDataI(pos, interactDir);
        if (InvUtil.findItem(stack -> stack.getItem() instanceof AxeItem)) {
            for (Direction dir : interactDir) {
                for (Vec3d clickVec : dirDataI.clickVecs(dir)) {
                    if (InvUtil.switchItem(stack -> stack.getItem() instanceof AxeItem)) {
                        BlockUtil.interactBlock(new PlaceData(dirDataI.placePos(),dir,clickVec,true,null));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean needFix(BlockPos pos, BlockState needState) {
        BlockState blockState = mc.world.getBlockState(pos);
        //不用内置规则替换
        Block needBlock = needState.getBlock();
        Block strippedBlock = AxeItem.STRIPPED_BLOCKS.get(blockState.getBlock());
        return needBlock.equals(strippedBlock);

    }
}
