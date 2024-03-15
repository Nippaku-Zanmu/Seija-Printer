package com.kijinseija.seija_printer.print_main.printer.placedata_getter.getter;

import com.kijinseija.seija_printer.print_main.printer.placedata_getter.AbstractDataGetter;
import com.kijinseija.seija_printer.print_main.printer.util.DirData;
import com.kijinseija.seija_printer.print_main.printer.util.PlaceData;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class HopperDataGetter extends AbstractDataGetter {
    @Override
    public PlaceData getData(BlockState needState, DirData dirData) {
        BlockPos pos = dirData.placePos();
        Direction direction = needState.get(Properties.HOPPER_FACING);
        for (Direction dir : dirData.dirs()) {
            Direction placeDir = dir == Direction.UP ? Direction.DOWN : dir;
            if (placeDir == direction) {
                //Vec3d hitVec = pos.toCenterPos().offset(dir, 0.5);
                for (Vec3d hitVec : dirData.clickVecs(dir)) {
                    return new PlaceData(pos.offset(dir), dir.getOpposite(), hitVec, true, null);
                }
            }
        }
        return new PlaceData(null, null, null, false, null);
    }

    @Override
    public boolean isSuitable(BlockState needState, BlockPos pos) {
        return needState.getBlock() instanceof HopperBlock;
    }
}
