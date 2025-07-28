package com.kijinseija.seija_printer.print_main.printer.block_fixer.fixers;

import com.kijinseija.seija_printer.print_main.printer.block_fixer.AbstractFixer;
import com.kijinseija.seija_printer.print_main.printer.util.BlockUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
import com.kijinseija.seija_printer.print_main.printer.util.records.DirDataI;
import com.kijinseija.seija_printer.print_main.printer.util.InvUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.AxeItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class StrippedFixer extends AbstractFixer {
    public StrippedFixer() {
        super("LogFix");
    }

    @Override
    public int fixBlock(BlockPos pos, BlockState needState) {
        List<Direction> interactDir = BlockUtil.getInteractDir(pos);
        DirData dirData = new DirData(pos, interactDir);
        if (InvUtil.findItem(stack -> stack.getItem() instanceof AxeItem)) {
            for (Direction dir : interactDir) {
                for (Vec3d clickVec : dirData.clickVecsInte(dir)) {
                    if (InvUtil.switchItem(stack -> stack.getItem() instanceof AxeItem)) {
                        BlockUtil.interactBlock(PlaceData.newInstance(dirData.placePos(),dir,clickVec,true,null));
                        return SUCCESS;
                    }else return RETURN;
                }
            }
        }
        return CONTINUE;
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
