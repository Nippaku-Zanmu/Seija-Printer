package com.kijinseija.seija_printer.printer.placedata_getter.getter;

import com.kijinseija.seija_printer.printer.util.PlaceData;
import com.kijinseija.seija_printer.printer.placedata_getter.AbstractDataGetter;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class PillarDataGetter extends AbstractDataGetter {
    //木头
    @Override
    public PlaceData getData(BlockPos pos, BlockState needState, List<Direction> dirs) {
        Direction.Axis needAxis = needState.get(Properties.AXIS);
        for (Direction dir : dirs) {
            Vec3d hitVec = pos.toCenterPos().offset(dir, 0.5);
            if (needAxis == dir.getOpposite().getAxis())
                return new PlaceData(pos.offset(dir), dir.getOpposite(), hitVec, true, null);

        }
        return new PlaceData(null, null, null, false, null);
    }

    @Override
    public boolean isSuitable(BlockState needState, BlockPos pos) {
        return needState.getBlock() instanceof PillarBlock;
    }
}
