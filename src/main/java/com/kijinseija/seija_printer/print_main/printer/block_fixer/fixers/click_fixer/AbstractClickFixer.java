package com.kijinseija.seija_printer.print_main.printer.block_fixer.fixers.click_fixer;

import com.kijinseija.seija_printer.print_main.printer.block_fixer.AbstractFixer;
import com.kijinseija.seija_printer.print_main.printer.util.BlockUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.DirDataI;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public abstract class AbstractClickFixer extends AbstractFixer {
    @Override
    public boolean fixBlock(BlockPos pos, BlockState needState) {
        List<Direction> interactDir = BlockUtil.getInteractDir(pos);
        if (interactDir.isEmpty())
            return false;
//        BlockUtil.interactBlock(pos,interactDir.get(0));
        DirDataI dirDataI = new DirDataI(pos, BlockUtil.getInteractDir(pos));
        for (Direction dir : dirDataI.dirs()) {
            for (Vec3d clickVec : dirDataI.clickVecs(dir)) {
                BlockUtil.interactBlock(new PlaceData(dirDataI.placePos(),dir,clickVec,true,null));
                return true;
            }
        }
        return false;
    }
}
