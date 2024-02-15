package com.kijinseija.seija_printer.printer.placedata_getter.getter;

import com.kijinseija.seija_printer.printer.util.PlaceData;
import com.kijinseija.seija_printer.printer.placedata_getter.AbstractDataGetter;
import net.minecraft.block.BlockState;
import net.minecraft.block.RodBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class ClickDirDataGetter extends AbstractDataGetter {
    //方块朝向与点击朝向相同
    //末地jb/铜jb/
    @Override
    public PlaceData getData(BlockPos pos, BlockState needState, List<Direction> dirs) {
        Direction needDir = needState.get(Properties.FACING);
        for (Direction dir : dirs) {
            Vec3d hitVec = pos.toCenterPos().offset(dir, 0.5);
            if (needDir.getOpposite() == dir)
                return new PlaceData(pos.offset(dir), dir.getOpposite(), hitVec, true, null);
        }
        return new PlaceData(null, null, null, false, null);
    }

    @Override
    public boolean isSuitable(BlockState needState, BlockPos pos) {
        return needState.getBlock() instanceof RodBlock
            ||needState.getBlock() instanceof ShulkerBoxBlock;
    }
}
