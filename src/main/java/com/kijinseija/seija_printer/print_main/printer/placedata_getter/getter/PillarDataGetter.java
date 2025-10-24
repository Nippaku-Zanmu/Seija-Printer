//package com.kijinseija.seija_printer.print_main.printer.placedata_getter.getter;
//
//import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
//import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
//import com.kijinseija.seija_printer.print_main.printer.placedata_getter.AbstractDataGetter;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.PillarBlock;
//import net.minecraft.state.property.Properties;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Direction;
//import net.minecraft.util.math.Vec3d;
//
//public class PillarDataGetter extends AbstractDataGetter {
//    //木头
//    @Override
//    public PlaceData getData(BlockState needState, DirData dirData) {
//        BlockPos pos = dirData.placePos();
//        Direction.Axis needAxis = needState.get(Properties.AXIS);
//        for (Direction dir : dirData.dirs()) {
//            for (Vec3d hitVec : dirData.clickVecs(dir)) {
//                if (needAxis == dir.getOpposite().getAxis())
//                    return new PlaceData(pos.offset(dir), dir.getOpposite(), hitVec, true, null);
//            }
//        }
//        return new PlaceData(null, null, null, false, null);
//    }
//
//    @Override
//    public boolean isSuitable(BlockState needState, BlockPos pos) {
//        return needState.getBlock() instanceof PillarBlock;
//    }
//}
