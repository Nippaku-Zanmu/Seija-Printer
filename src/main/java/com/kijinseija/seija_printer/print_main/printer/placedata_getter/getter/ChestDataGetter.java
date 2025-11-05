/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

//package com.kijinseija.seija_printer.print_main.printer.placedata_getter.getter;
//
//import com.kijinseija.seija_printer.print_main.modules.Printer;
//import com.kijinseija.seija_printer.print_main.printer.placedata_getter.AbstractDataGetter;
//import com.kijinseija.seija_printer.print_main.printer.util.BlockRotDataGetter;
//import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
//import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
//import com.kijinseija.seija_printer.print_main.printer.util.RenderHelper;
//import com.kijinseija.seija_printer.print_main.printer.util.SeijaUtil;
//import com.kijinseija.seija_printer.print_main.printer.util.records.RotationData;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.ChestBlock;
//import net.minecraft.block.enums.ChestType;
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.state.property.Properties;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Direction;
//import net.minecraft.util.math.Vec3d;
//
//public class ChestDataGetter extends AbstractDataGetter {
//    MinecraftClient mc = MinecraftClient.getInstance();
//
//    @Override
//    public PlaceData getData(BlockState needState, DirData dirData) {
//        BlockPos pos = dirData.placePos();
//        Direction needDir = needState.get(Properties.HORIZONTAL_FACING);
//        ChestType needType = needState.get(Properties.CHEST_TYPE);
//
//        Direction neiOffset = getNeiOffset(needDir, needType);
//        BlockState neiState = mc.world.getBlockState(pos.offset(neiOffset == null ? Direction.UP : neiOffset));
//
//        RotationData rotData = BlockRotDataGetter.getRotData(needState);
//        boolean b = neiOffset != null && neiState.getBlock().equals(needState.getBlock())
//            && neiState.get(Properties.HORIZONTAL_FACING).equals(needDir);
//        //邻近状态
//        if (!b)
//            needType = ChestType.SINGLE;
//        //另外一般没放的话就设为单个进行计算
//
//
//        for (Direction dir : dirData.dirs()) {
//            //Vec3d clickVec = pos.toCenterPos().offset(dir, 0.5);
//            for (Vec3d clickVec : dirData.clickVecs(dir)) {
//
//
//                if (needType == ChestType.SINGLE
//                    && (mc.world.getBlockState(pos.offset(dir)).getBlock() != needState.getBlock()
//                    || (mc.world.getBlockState(pos.offset(dir)).getBlock() != needState.getBlock()
//                    && mc.world.getBlockState(pos.offset(dir)).get(Properties.HORIZONTAL_FACING) != needDir))
//
//                    && (rotData==null||needDir == Direction.fromRotation(SeijaUtil.getYaw(clickVec)).getOpposite())) {
//
////                    if (neiOffset != null) {
////                        Printer.getINSTANCE().blackList.add(RenderHelper.getBlackInfo(pos.offset(neiOffset)));
////                    }
//
//                    return new PlaceData(pos.offset(dir), dir.getOpposite(), clickVec, true, rotData);
//                } else if (needType != ChestType.SINGLE && neiOffset == dir)
//                    return new PlaceData(pos.offset(dir), dir.getOpposite(), clickVec, true, null);
//
////            PlaceData testData = new PlaceData(pos.offset(dir), dir.getOpposite(), clickVec, true, null);
////            CData cData = getCData(testData, pri.needBlockReplace(needState));
////            if (cData.chestType == needType&&cData.dir == needDir)return testData;
//            }
//        }
//        return new PlaceData(null, null, null, false, null);
//    }
//
//    @Override
//    public boolean isSuitable(BlockState needState, BlockPos pos) {
//        return needState.getBlock() instanceof ChestBlock;
//    }
//
//
//    public Direction getNeiOffset(Direction cDir, ChestType t) {
//        switch (t) {
//            case LEFT -> {
//                return cDir.rotateYClockwise();
//            }
//            case RIGHT -> {
//                return cDir.rotateYCounterclockwise();
//            }
//        }
//        return null;
//    }
//}
