//package com.kijinseija.seija_printer.print_main.printer.placedata_getter.getter;
//
//import com.kijinseija.seija_printer.print_main.printer.util.BlockRotDataGetter;
//import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
//import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
//import com.kijinseija.seija_printer.print_main.printer.placedata_getter.AbstractDataGetter;
//import com.kijinseija.seija_printer.print_main.printer.util.SeijaUtil;
//import com.kijinseija.seija_printer.print_main.printer.util.records.RotationData;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.WallMountedBlock;
//import net.minecraft.block.enums.BlockFace;
//
//import net.minecraft.state.property.Properties;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Direction;
//import net.minecraft.util.math.Vec3d;
//
//public class WallMountedDataGetter extends AbstractDataGetter {
//    //按钮 拉杆
//    @Override
//    public PlaceData getData(BlockState needState, DirData dirData) {
//        BlockPos pos = dirData.placePos();
//        BlockFace wallMountLocation = needState.get(Properties.BLOCK_FACE);
//        RotationData rotData = BlockRotDataGetter.getRotData(needState);
//
//        Direction needDir = needState.get(Properties.HORIZONTAL_FACING);
//        for (Direction dir : dirData.dirs()) {
//            for (Vec3d hitVec : dirData.clickVecs(dir)) {
//                if (wallMountLocation == BlockFace.WALL) {
//                    if (needDir.getOpposite() == dir)
//                        return new PlaceData(pos.offset(dir), dir.getOpposite(), hitVec, true, null);
//                } else if (needDir == Direction.fromRotation(SeijaUtil.getYaw(hitVec))) {
//                    if (wallMountLocation == BlockFace.FLOOR
//                        && dir == Direction.DOWN
//                    )
//                        return new PlaceData(pos.offset(dir), dir.getOpposite(), hitVec, true, null);
//                    else if (wallMountLocation == BlockFace.CEILING && dir == Direction.UP) {
//                        return new PlaceData(pos.offset(dir), dir.getOpposite(), hitVec, true, null);
//                    }
//                }
//            }
//        }
//        return new PlaceData(null, null, null, false, null);
//    }
//
//    @Override
//    public boolean isSuitable(BlockState needState, BlockPos pos) {
//        return needState.getBlock() instanceof WallMountedBlock;
//    }
//}
