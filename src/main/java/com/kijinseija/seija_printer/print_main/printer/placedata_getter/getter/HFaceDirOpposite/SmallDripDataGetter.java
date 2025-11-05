/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

//package com.kijinseija.seija_printer.print_main.printer.placedata_getter.getter.HFaceDirOpposite;
//
//import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
//import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.Blocks;
//import net.minecraft.block.SmallDripleafBlock;
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.fluid.Fluids;
//import net.minecraft.registry.tag.BlockTags;
//import net.minecraft.util.math.BlockPos;
//
//public class SmallDripDataGetter extends HFaceDirOppositeDataGetter {
//    private static final MinecraftClient mc = MinecraftClient.getInstance();
//
//    @Override
//    public PlaceData getData(BlockState needState, DirData dirData) {
//        BlockPos pos = dirData.placePos();
//        BlockState state = mc.world.getBlockState(pos.down());
//        if (mc.world.getFluidState(pos).isEqualAndStill(Fluids.WATER) && (state.isIn(BlockTags.SMALL_DRIPLEAF_PLACEABLE) || state.isIn(BlockTags.DIRT) || state.isOf(Blocks.FARMLAND)))
//            return super.getData(needState, dirData);
//        return PlaceData.NULL;
//    }
//
//    @Override
//    public boolean isSuitable(BlockState needState, BlockPos pos) {
//        return needState.getBlock() instanceof SmallDripleafBlock;
//    }
//}
