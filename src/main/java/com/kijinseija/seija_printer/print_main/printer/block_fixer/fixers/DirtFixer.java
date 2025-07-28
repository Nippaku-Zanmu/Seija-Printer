package com.kijinseija.seija_printer.print_main.printer.block_fixer.fixers;

import com.kijinseija.seija_printer.print_main.printer.block_fixer.AbstractFixer;
import com.kijinseija.seija_printer.print_main.printer.util.BlockUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
import com.kijinseija.seija_printer.print_main.printer.util.records.DirDataI;
import com.kijinseija.seija_printer.print_main.printer.util.InvUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
import net.minecraft.block.*;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class DirtFixer extends AbstractFixer {
    public DirtFixer() {
        super("DirtFix");
    }

    @Override
    public int fixBlock(BlockPos pos, BlockState needState) {

        DirData dirData = new DirData(pos, BlockUtil.getInteractDir(pos));

        dirData.dirs().remove(Direction.DOWN);
        for (Direction dir : dirData.dirs()) {
            for (Vec3d clickVec : dirData.clickVecsInte(dir)) {
                if (InvUtil.switchItem(stack -> needState.getBlock() instanceof DirtPathBlock
                    ? stack.getItem() instanceof ShovelItem : stack.getItem() instanceof HoeItem)) {
                    BlockUtil.interactBlock(PlaceData.newInstance(dirData.placePos(), dir, clickVec, true, null));
                    return AbstractFixer.SUCCESS;
                } else return AbstractFixer.RETURN;
            }
        }
        return AbstractFixer.CONTINUE;
    }

    @Override
    public boolean needFix(BlockPos pos, BlockState needState) {
        Block block = mc.world.getBlockState(pos).getBlock();
        if (needState.getBlock() instanceof DirtPathBlock && !InvUtil.findItem(stack -> stack.getItem() instanceof ShovelItem))
            return false;
        if (needState.getBlock() instanceof FarmlandBlock && !InvUtil.findItem(stack -> stack.getItem() instanceof HoeItem))
            return false;
        return ((needState.getBlock() instanceof DirtPathBlock || needState.getBlock() instanceof FarmlandBlock)
            && (block instanceof SpreadableBlock || block.equals(Blocks.DIRT)
            || block.equals(Blocks.ROOTED_DIRT) || block.equals(Blocks.PODZOL)));
    }
}
