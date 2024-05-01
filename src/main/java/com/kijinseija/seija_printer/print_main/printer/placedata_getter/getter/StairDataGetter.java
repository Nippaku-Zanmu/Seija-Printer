package com.kijinseija.seija_printer.print_main.printer.placedata_getter.getter;

import com.kijinseija.seija_printer.print_main.printer.util.BlockRotDataGetter;
import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
import com.kijinseija.seija_printer.print_main.printer.placedata_getter.AbstractDataGetter;
import com.kijinseija.seija_printer.print_main.printer.util.SeijaUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.RotationData;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class StairDataGetter extends AbstractDataGetter {
    //楼梯
    @Override
    public PlaceData getData(BlockState needState, DirData dirData) {
        BlockPos pos = dirData.placePos();
        BlockHalf blockHalf = needState.get(StairsBlock.HALF);
        Direction direction = needState.get(StairsBlock.FACING);
        ArrayList<Direction> directions = new ArrayList<>(dirData.dirs());
        RotationData rotData = BlockRotDataGetter.getRotData(needState);
        switch (blockHalf) {
            case TOP -> directions.remove(Direction.DOWN);
            case BOTTOM -> directions.remove(Direction.UP);
        }
        for (Direction dir : directions) {
            for (Vec3d hitVec : dirData.clickVecs(dir, blockHalf == BlockHalf.TOP ? 1 : 2)) {
                if (dir != Direction.UP && dir != Direction.DOWN)
                    if (blockHalf == BlockHalf.TOP) hitVec = hitVec.add(0, 0.1, 0);
                    else hitVec = hitVec.add(0, -0.1, 0);

                if (Direction.fromRotation(SeijaUtil.getYaw(hitVec)) == direction || rotData != null) {
                    return new PlaceData(pos.offset(dir), dir.getOpposite(), hitVec, true, rotData);
                }
            }
//            Vec3d hitVec = pos.toCenterPos().offset(dir, 0.5);

        }
        return new PlaceData(null, null, null, false, null);
    }


    @Override
    public boolean isSuitable(BlockState needState, BlockPos pos) {
        return needState.getBlock() instanceof StairsBlock;
    }
}
