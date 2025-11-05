/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

//package com.kijinseija.seija_printer.print_main.printer.placedata_getter.getter;
//
//import com.kijinseija.seija_printer.print_main.printer.placedata_getter.AbstractDataGetter;
//import com.kijinseija.seija_printer.print_main.printer.util.BlockRotDataGetter;
//import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
//import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
//import com.kijinseija.seija_printer.print_main.printer.util.SeijaUtil;
//import com.kijinseija.seija_printer.print_main.printer.util.records.RotationData;
//import net.minecraft.block.Block;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.SignBlock;
//import net.minecraft.block.WallSignBlock;
//import net.minecraft.state.property.Properties;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Direction;
//import net.minecraft.util.math.RotationPropertyHelper;
//import net.minecraft.util.math.Vec3d;
//
//public class SignDataGetter extends AbstractDataGetter {
//    @Override
//    public PlaceData getData(BlockState needState, DirData dirData) {
//        BlockPos pos = dirData.placePos();
//        if (needState.getBlock() instanceof WallSignBlock) {
//            Direction needDir = needState.get(Properties.HORIZONTAL_FACING);
//            for (Direction dir : dirData.dirs()) {
//                if (dir.getAxis() == Direction.Axis.Y || dir.getOpposite() != needDir) continue;
////                Vec3d clickVec = pos.toCenterPos().offset(dir, 0.5);
//                for (Vec3d clickVec : dirData.clickVecs(dir)) {
//                    return new PlaceData(pos.offset(dir), dir.getOpposite(), clickVec, true, null);
//                }
//            }
//
//        } else if (needState.getBlock() instanceof SignBlock && dirData.dirs().contains(Direction.DOWN)) {
//            RotationData rotData = BlockRotDataGetter.getRotData(needState);
//            Integer rotation = needState.get(Properties.ROTATION);
//            for (Vec3d hitVec : dirData.clickVecs(Direction.DOWN)) {
//
//                if (RotationPropertyHelper.fromYaw((float) (SeijaUtil.getYaw(hitVec) + 180.0f)) == rotation||rotData!=null) {
//                    return new PlaceData(pos.offset(Direction.DOWN), Direction.UP, hitVec, true, rotData);
//                }
//            }
//        }
//        return new PlaceData(null, null, null, false, null);
//    }
//
//    @Override
//    public boolean isSuitable(BlockState needState, BlockPos pos) {
//
//        Block needBlock = needState.getBlock();
//        return needBlock instanceof SignBlock
//            || needBlock instanceof WallSignBlock;
//    }
//}
