package com.kijinseija.seija_printer.printer.placedata_getter.getter;

import com.kijinseija.seija_printer.printer.util.PlaceData;
import com.kijinseija.seija_printer.printer.placedata_getter.AbstractDataGetter;
import com.kijinseija.seija_printer.printer.util.SeijaUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class StairDataGetter extends AbstractDataGetter {
    //楼梯
    @Override
    public PlaceData getData(BlockPos pos, BlockState needState, List<Direction> dirs) {
        BlockHalf blockHalf = needState.get(StairsBlock.HALF);
        Direction direction = needState.get(StairsBlock.FACING);
        ArrayList<Direction> directions = new ArrayList<>(dirs);
        switch (blockHalf) {
            case TOP -> directions.remove(Direction.DOWN);
            case BOTTOM -> directions.remove(Direction.UP);
        }
        for (Direction dir : directions) {
            Vec3d hitVec = pos.toCenterPos().offset(dir, 0.5);
            if (dir != Direction.UP && dir != Direction.DOWN)
                if (blockHalf == BlockHalf.TOP) hitVec = hitVec.add(0, 0.1, 0);
                else hitVec = hitVec.add(0, -0.1, 0);

            if (Direction.fromRotation(SeijaUtil.getYaw(hitVec)) == direction) {
                return new PlaceData(pos.offset(dir), dir.getOpposite(), hitVec, true, null);
            }
        }
        return new PlaceData(null, null, null, false, null);
    }


    @Override
    public boolean isSuitable(BlockState needState, BlockPos pos) {
        return needState.getBlock() instanceof StairsBlock;
    }
}
