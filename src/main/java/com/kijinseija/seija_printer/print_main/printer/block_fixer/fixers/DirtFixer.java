package com.kijinseija.seija_printer.print_main.printer.block_fixer.fixers;

import com.kijinseija.seija_printer.print_main.printer.block_fixer.AbstractFixer;
import com.kijinseija.seija_printer.print_main.printer.util.BlockUtil;
import com.kijinseija.seija_printer.print_main.printer.util.DirDataI;
import com.kijinseija.seija_printer.print_main.printer.util.InvUtil;
import com.kijinseija.seija_printer.print_main.printer.util.PlaceData;
import net.minecraft.block.*;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class DirtFixer extends AbstractFixer {
    @Override
    public boolean fixBlock(BlockPos pos, BlockState needState) {

        DirDataI dirDataI = new DirDataI(pos, BlockUtil.getInteractDir(pos));
        if (needState.getBlock() instanceof DirtPathBlock)
            dirDataI.dirs().remove(Direction.DOWN);
        for (Direction dir : dirDataI.dirs()) {
            for (Vec3d clickVec : dirDataI.clickVecs(dir)) {
                if (InvUtil.switchItem(stack -> needState.getBlock() instanceof DirtPathBlock
                    ? stack.getItem() instanceof ShovelItem : stack.getItem() instanceof HoeItem)) {
                    BlockUtil.interactBlock(new PlaceData(dirDataI.placePos(),dir,clickVec,true,null));
                    return true;
                }
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
