package com.kijinseija.seija_printer.print_main.printer.placedata_getter.getter;

import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
import com.kijinseija.seija_printer.print_main.printer.placedata_getter.AbstractDataGetter;
import net.minecraft.block.BlockState;
import net.minecraft.block.RodBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class ClickDirDataGetter extends AbstractDataGetter {
    //方块朝向与点击朝向相同
    //末地jb/铜jb/
    @Override
    public PlaceData getData(BlockState needState, DirData dirData) {
        BlockPos pos = dirData.placePos();
        Direction needDir = needState.get(Properties.FACING);
        for (Direction dir : dirData.dirs()) {
            for (Vec3d hitVec : dirData.clickVecs(dir)) {
                if (needDir.getOpposite() == dir)
                    return new PlaceData(pos.offset(dir), dir.getOpposite(), hitVec, true, null);
            }
        }
        return new PlaceData(null, null, null, false, null);
    }

    @Override
    public boolean isSuitable(BlockState needState, BlockPos pos) {
        return needState.getBlock() instanceof RodBlock
            || needState.getBlock() instanceof ShulkerBoxBlock;
    }
}
