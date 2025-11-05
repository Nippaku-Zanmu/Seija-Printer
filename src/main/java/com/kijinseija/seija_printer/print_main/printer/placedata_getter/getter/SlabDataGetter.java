/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

//package com.kijinseija.seija_printer.print_main.printer.placedata_getter.getter;
//
//import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
//import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
//import com.kijinseija.seija_printer.print_main.printer.placedata_getter.AbstractDataGetter;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.SlabBlock;
//import net.minecraft.block.enums.SlabType;
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Direction;
//import net.minecraft.util.math.Vec3d;
//
//import java.util.ArrayList;
//
//public class SlabDataGetter extends AbstractDataGetter {
//    MinecraftClient mc = MinecraftClient.getInstance();
//
//    @Override
//    public PlaceData getData(BlockState needState, DirData dirData) {
//        BlockPos pos = dirData.placePos();
//        SlabType slabType = needState.get(SlabBlock.TYPE);
//        if (slabType == SlabType.DOUBLE) {
//            PlaceData data = getData(needState.with(SlabBlock.TYPE, SlabType.BOTTOM), dirData);
//            if (data.valid()) return data;
//            else return getData(needState.with(SlabBlock.TYPE, SlabType.TOP), dirData);
//        }
//        ArrayList<Direction> dirs1 = new ArrayList<>(dirData.dirs());
//        switch (slabType) {
//            case BOTTOM -> dirs1.remove(Direction.UP);
//            case TOP -> dirs1.remove(Direction.DOWN);
//        }
//        for (Direction direction : dirs1) {
//            BlockState helperState = mc.world.getBlockState(pos.offset(direction));
//            if (helperState.getBlock().equals(needState.getBlock())
//                && (
//                (helperState.get(SlabBlock.TYPE) != SlabType.DOUBLE
//                    && helperState.get(SlabBlock.TYPE) != slabType
//                    && direction.getAxis() != Direction.Axis.Y)
//                    ||
//                    (helperState.get(SlabBlock.TYPE) != SlabType.DOUBLE &&
//                        helperState.get(SlabBlock.TYPE) == slabType
//                        && direction.getAxis() == Direction.Axis.Y))
//            ) continue;
//            if (direction.getAxis() == Direction.Axis.Y) {
//                for (Vec3d clickVec : dirData.clickVecs(direction)) {
//                    return new PlaceData(pos.offset(direction), direction.getOpposite()
//                        , clickVec, true, null);
//                }
//
//            } else {
//                for (Vec3d clickVec : dirData.clickVecs(direction, slabType == SlabType.BOTTOM ? 2 : 1)) {
//                    return new PlaceData(pos.offset(direction), direction.getOpposite()
//                        , clickVec, true, null);
//
//                }
//            }
//        }
//        return new PlaceData(null, null, null, false, null);
//    }
//
//
//    @Override
//    public boolean isSuitable(BlockState needState, BlockPos pos) {
//        return needState.getBlock() instanceof SlabBlock;
//    }
//}
