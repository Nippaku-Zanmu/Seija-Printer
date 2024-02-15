package com.kijinseija.seija_printer.printer.placedata_getter.getter;

import com.kijinseija.seija_printer.printer.util.PlaceData;
import com.kijinseija.seija_printer.printer.placedata_getter.AbstractDataGetter;
import com.kijinseija.seija_printer.printer.util.SeijaUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class TrapdoorDataGetter extends AbstractDataGetter {
    //活版门
    @Override
    public PlaceData getData(BlockPos pos, BlockState needState, List<Direction> dirs) {
        Direction fac = needState.get(Properties.HORIZONTAL_FACING);
        BlockHalf blockHalf = needState.get(Properties.BLOCK_HALF);
        for (Direction dir : dirs) {
            Vec3d hitVec = pos.toCenterPos().offset(dir, 0.5);
            switch (dir) {
                case UP:
                    if (Direction.fromRotation(SeijaUtil.getYaw(hitVec)).getOpposite()
                        == fac && blockHalf == BlockHalf.TOP)
                        return new PlaceData(pos.offset(dir), dir.getOpposite(), hitVec, true, null);
                    break;
                case DOWN:
                    if (Direction.fromRotation(SeijaUtil.getYaw(hitVec)).getOpposite()
                        == fac && blockHalf == BlockHalf.BOTTOM)
                        return new PlaceData(pos.offset(dir), dir.getOpposite(), hitVec, true, null);
                    break;
                default:
                    if (dir.getOpposite() == fac) {
                        switch (blockHalf) {
                            case TOP -> {
                                return new PlaceData(pos.offset(dir), dir.getOpposite(), hitVec.add(0, 0.1, 0), true, null);
                            }
                            case BOTTOM -> {
                                return new PlaceData(pos.offset(dir), dir.getOpposite(), hitVec.add(0, -0.1, 0), true, null);
                            }
                        }
                    }
            }
        }
        return new PlaceData(null,null,null,false,null);
    }

    @Override
    public boolean isSuitable(BlockState needState, BlockPos pos) {
        return needState.getBlock()instanceof TrapdoorBlock;
    }

}
