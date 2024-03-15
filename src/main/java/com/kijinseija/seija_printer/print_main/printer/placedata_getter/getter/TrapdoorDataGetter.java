package com.kijinseija.seija_printer.print_main.printer.placedata_getter.getter;

import com.kijinseija.seija_printer.print_main.printer.util.DirData;
import com.kijinseija.seija_printer.print_main.printer.util.PlaceData;
import com.kijinseija.seija_printer.print_main.printer.placedata_getter.AbstractDataGetter;
import com.kijinseija.seija_printer.print_main.printer.util.SeijaUtil;
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
    public PlaceData getData(BlockState needState, DirData dirData) {
        BlockPos pos = dirData.placePos();

        Direction fac = needState.get(Properties.HORIZONTAL_FACING);
        BlockHalf blockHalf = needState.get(Properties.BLOCK_HALF);
        for (Direction dir : dirData.dirs()) {
            for (Vec3d hitVec : dirData.clickVecs(dir, blockHalf == BlockHalf.TOP ? 1 : 2)) {


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
                            return new PlaceData(pos.offset(dir), dir.getOpposite(), hitVec, true, null);
//                            switch (blockHalf) {
//                                case TOP -> {
//                                    return new PlaceData(pos.offset(dir), dir.getOpposite(), hitVec.add(0, 0.1, 0), true, null);
//                                }
//                                case BOTTOM -> {
//                                    return new PlaceData(pos.offset(dir), dir.getOpposite(), hitVec.add(0, -0.1, 0), true, null);
//                                }
//                            }
                        }
                }
            }
        }
        return new PlaceData(null, null, null, false, null);
    }

    @Override
    public boolean isSuitable(BlockState needState, BlockPos pos) {
        return needState.getBlock() instanceof TrapdoorBlock;
    }

}
