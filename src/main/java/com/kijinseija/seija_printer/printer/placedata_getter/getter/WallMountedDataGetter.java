package com.kijinseija.seija_printer.printer.placedata_getter.getter;

import com.kijinseija.seija_printer.printer.util.PlaceData;
import com.kijinseija.seija_printer.printer.placedata_getter.AbstractDataGetter;
import com.kijinseija.seija_printer.printer.util.SeijaUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class WallMountedDataGetter extends AbstractDataGetter {
    //按钮 拉杆
    @Override
    public PlaceData getData(BlockPos pos, BlockState needState, List<Direction> dirs) {
        WallMountLocation wallMountLocation = needState.get(Properties.WALL_MOUNT_LOCATION);
        Direction needDir = needState.get(Properties.HORIZONTAL_FACING);
        for (Direction dir : dirs) {
            Vec3d hitVec = pos.toCenterPos().offset(dir, 0.5);
            if (wallMountLocation == WallMountLocation.WALL) {
                if (needDir.getOpposite() == dir)
                    return new PlaceData(pos.offset(dir), dir.getOpposite(), hitVec, true, null);
            } else if (needDir == Direction.fromRotation(SeijaUtil.getYaw(hitVec))) {
                if (wallMountLocation == WallMountLocation.FLOOR
                    && dir == Direction.DOWN
                )
                    return new PlaceData(pos.offset(dir), dir.getOpposite(), hitVec, true, null);
                else if (wallMountLocation == WallMountLocation.CEILING && dir == Direction.UP) {
                    return new PlaceData(pos.offset(dir), dir.getOpposite(), hitVec, true, null);
                }
            }
        }
        return new PlaceData(null, null, null, false, null);
    }

    @Override
    public boolean isSuitable(BlockState needState, BlockPos pos) {
        return needState.getBlock() instanceof WallMountedBlock;
    }
}
