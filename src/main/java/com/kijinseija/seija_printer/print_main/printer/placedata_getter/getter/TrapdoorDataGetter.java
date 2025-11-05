/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

//package com.kijinseija.seija_printer.print_main.printer.placedata_getter.getter;
//
//import com.kijinseija.seija_printer.print_main.printer.util.BlockRotDataGetter;
//import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
//import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
//import com.kijinseija.seija_printer.print_main.printer.placedata_getter.AbstractDataGetter;
//import com.kijinseija.seija_printer.print_main.printer.util.SeijaUtil;
//import com.kijinseija.seija_printer.print_main.printer.util.records.RotationData;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.TrapdoorBlock;
//import net.minecraft.block.enums.BlockHalf;
//import net.minecraft.state.property.Properties;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Direction;
//import net.minecraft.util.math.Vec3d;
//
//public class TrapdoorDataGetter extends AbstractDataGetter {
//    //活版门
//    @Override
//    public PlaceData getData(BlockState needState, DirData dirData) {
//        BlockPos pos = dirData.placePos();
//
//        Direction fac = needState.get(Properties.HORIZONTAL_FACING);
//        BlockHalf blockHalf = needState.get(Properties.BLOCK_HALF);
//        RotationData rotData = BlockRotDataGetter.getRotData(needState);
//        for (Direction dir : dirData.dirs()) {
//            for (Vec3d hitVec : dirData.clickVecs(dir, blockHalf == BlockHalf.TOP ? 1 : 2)) {
//
//
//
//                switch (dir) {
//                    case UP:
//                        if ((Direction.fromRotation(SeijaUtil.getYaw(hitVec)).getOpposite()
//                            == fac||rotData!=null) && blockHalf == BlockHalf.TOP)
//                            return new PlaceData(pos.offset(dir), dir.getOpposite(), hitVec, true, rotData);
//                        break;
//                    case DOWN:
//                        if ((Direction.fromRotation(SeijaUtil.getYaw(hitVec)).getOpposite()
//                            == fac||rotData!=null) && blockHalf == BlockHalf.BOTTOM)
//                            return new PlaceData(pos.offset(dir), dir.getOpposite(), hitVec, true, rotData);
//                        break;
//                    default:
//                        if (dir.getOpposite() == fac) {
//                            return new PlaceData(pos.offset(dir), dir.getOpposite(), hitVec, true, rotData);
//
//                        }
//                }
//            }
//        }
//        return new PlaceData(null, null, null, false, null);
//    }
//
//    @Override
//    public boolean isSuitable(BlockState needState, BlockPos pos) {
//        return needState.getBlock() instanceof TrapdoorBlock;
//    }
//
//}
