/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer.state_decide.MainDecide;
import com.kijinseija.seija_printer.print_main.printer.util.BlockRotDataGetter;
import com.kijinseija.seija_printer.print_main.printer.util.BlockUtil;
import com.kijinseija.seija_printer.print_main.printer.util.InvUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.*;
import com.kijinseija.seija_printer.utils.player.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import java.util.function.BooleanSupplier;

public class DataGetter {

    private static final Minecraft mc = Minecraft.getInstance();
    private static final Printer pri = Printer.getINSTANCE();

    public static PlaceDataPack getData(BlockState needState, DirData data, ItemStack stack) {
        if (needState.getBlock() instanceof LiquidBlock) return PlaceDataPack.NULL;

        PlaceDataPack dataPla = getd(needState, data, stack,false);
        if (dataPla.data().valid()) {
            return dataPla;
        }

        return getd(needState, data, stack,true);
    }

    /**
     * getd
     *
     * @param needState needState
     * @param data      data
     * @param stack     stack
     * @param mode      true: 与自身位置交互 false 与相邻方块交互
     * @return {@link PlaceDataPack}
     * @see PlaceDataPack
     */
    private static PlaceDataPack getd(BlockState needState, DirData data, ItemStack stack, boolean mode) {
        if (mode)data = new DirData(data.placePos(), BlockUtil.getSortedDirs(data.placePos(),true));
        BlockPos placePos = data.placePos();


        if (needState.getBlock().equals(mc.level.getBlockState(placePos).getBlock()))
            return PlaceDataPack.NULL;

        if (mode && !canInte(placePos)) {
            return PlaceDataPack.NULL;
        }
        RotationData rData = BlockRotDataGetter.getRotData(needState);
        fD:
        for (Direction offDir : data.dirs()) {
            fV:
            for (Vec3 clickVec : mode ? data.clickVecsInte(offDir) : data.clickVecs(offDir)) {
                BlockPlaceContext placeContext = mode ? FakePlacementContext.getInstanceInte(clickVec, placePos, offDir, stack, rData)
                    : FakePlacementContext.getInstancePlac(clickVec, placePos, offDir, stack, rData);

                if (!mode && !mc.level.getBlockState(data.placePos()).canBeReplaced(placeContext)) {

                    continue fD;
                }

                if (!placeContext.canPlace()) {

                    continue fD;
                }

                BlockItem bItem;
                try {
                    bItem = (BlockItem) InvUtil.getItemFormBlock(needState.getBlock());
                    if (bItem == null) {

                        return PlaceDataPack.NULL;
                    }
                } catch (ClassCastException ignore) {
                    return PlaceDataPack.NULL;
                }
                Block needBlock = bItem.getBlock();
                placeContext = bItem.updatePlacementContext(placeContext);
                if (placeContext == null) {

                    continue;
                };
                if (!needBlock.isEnabled(placeContext.getLevel().enabledFeatures())) {

                    continue;
                }
                if (!placeContext.canPlace()) {

                    continue fD;
                }//test
                BlockState placementState = bItem.getPlacementState(placeContext);
                if (placementState == null) {

                    continue;
                }
                if (!MainDecide.INSTANCE.test(needState, placementState, placePos)) {

                    continue fV;
                }
                BooleanSupplier verify = () -> {
                    FakePlacementContext contextVerify = BlockStateVerify.getContextVerify(clickVec, mode ? placePos : placePos.relative(offDir), mode ? offDir : offDir.getOpposite(), stack, rData);
                    if (contextVerify.getClickedPos().equals(placePos) && contextVerify.getClickedFace().equals(mode ? offDir : offDir.getOpposite())) {
                        BlockState currentState = BlockStateVerify.genBlockState(contextVerify, needBlock);
                        if (currentState==null) {
                            return false;
                        }
                        return MainDecide.INSTANCE.test(needState, currentState, placePos);
                    }
                    //ChatUtils.sendMsg(Text.of("RDir " + contextVerify.getSide() + "TDir: " + offDir.getOpposite()));
                    //ChatUtils.sendMsg(Text.of("RPos " + contextVerify.getBlockPos() + "TPos: " + placePos.offset(offDir)));
                    return false;
                };
                if (mode)
                    return PlaceDataPack.inte(new PlaceData(placePos, offDir, clickVec, true, rData, verify));
                else
                    return PlaceDataPack.plac(new PlaceData(placePos.relative(offDir), offDir.getOpposite(), clickVec, true, rData, verify));
            }
        }

        return PlaceDataPack.NULL;
    }

    private static PlaceDataPack getDataInt(BlockState needState, DirData data, ItemStack stack) {
        BlockPos placePos = data.placePos();
        if (needState.getBlock().equals(mc.level.getBlockState(placePos).getBlock())) {
            return PlaceDataPack.NULL;
        }
        if (!canInte(placePos)) {
            return PlaceDataPack.NULL;
        }

        RotationData rData = BlockRotDataGetter.getRotData(needState);
        fD:
        for (Direction offDir : data.dirs()) {
            fV:
            for (Vec3 clickVec : data.clickVecsInte(offDir)) {
                BlockPlaceContext placeContext = FakePlacementContext.getInstanceInte(clickVec, placePos, offDir, stack, rData);

                if (!mc.level.getBlockState(data.placePos()).canBeReplaced(placeContext)) {
                    continue fD;
                }
                BlockItem bItem;
                try {
                    bItem = (BlockItem) InvUtil.getItemFormBlock(needState.getBlock());
                    if (bItem == null) return PlaceDataPack.NULL;
                } catch (ClassCastException ignore) {
                    return PlaceDataPack.NULL;
                }
                final Block needBlock = bItem.getBlock();
                placeContext = bItem.updatePlacementContext(placeContext);
                //重新赋值 mojang在BlockItem L74这样写的
                if (placeContext == null) continue;
                if (!needBlock.isEnabled(placeContext.getLevel().enabledFeatures())) {
                    continue;
                }
                if (!placeContext.canPlace()) {
                    continue fD;
                }//test
                BlockState placementState = bItem.getPlacementState(placeContext);
                if (placementState == null) {
                    continue;
                }
                if (!MainDecide.INSTANCE.test(needState, placementState, placePos)) {
                    continue fV;
                }
                BooleanSupplier verify = () -> {
                    FakePlacementContext contextVerify = BlockStateVerify.getContextVerify(clickVec, placePos, offDir, stack, rData);
                    if (contextVerify.getClickedPos().equals(placePos) && contextVerify.getClickedFace().equals(offDir)) {
                        BlockState currentState = BlockStateVerify.genBlockState(contextVerify, needBlock);
                        return MainDecide.INSTANCE.test(needState, currentState, placePos);
                    }
                    ChatUtils.sendMsg(Component.nullToEmpty("RDir " + contextVerify.getClickedFace() + "TDir: " + offDir.getOpposite()));
                    ChatUtils.sendMsg(Component.nullToEmpty("RPos " + contextVerify.getClickedPos() + "TPos: " + placePos.relative(offDir)));
                    return false;
                };
                return PlaceDataPack.inte(new PlaceData(placePos, offDir, clickVec, true, rData, verify));
            }
        }
        return PlaceDataPack.NULL;
    }

    private static boolean canInte(BlockPos pos) {
        Block block = mc.level.getBlockState(pos).getBlock();
        if (((block instanceof AirBlock) || (block instanceof BaseFireBlock)) && pri.bSetAirPlace.get()) {
            return true;
        }
        if ((block instanceof LiquidBlock) && (pri.bSetAirPlace.get() || pri.bSetLiquidInt.get())) {
            return true;
        }
        return (!BlockUtil.canPlaceIn(pos)) || block instanceof SlabBlock;
    }

    private static PlaceDataPack getDataPla(BlockState needState, DirData data, ItemStack stack) {
        BlockPos placePos = data.placePos();


        if (needState.getBlock().equals(mc.level.getBlockState(placePos).getBlock()))
            return PlaceDataPack.NULL;
        RotationData rData = BlockRotDataGetter.getRotData(needState);
        fD:
        for (Direction offDir : data.dirs()) {
            fV:
            for (Vec3 clickVec : data.clickVecs(offDir)) {
                BlockPlaceContext placeContext = FakePlacementContext.getInstancePlac(clickVec, placePos, offDir, stack, rData);

                if (!placeContext.canPlace()) {
                    continue fD;
                }

                //检测支撑方块是否会被替换 若会被替换则结束
                BlockItem bItem;
                try {
                    bItem = (BlockItem) InvUtil.getItemFormBlock(needState.getBlock());
                    if (bItem == null) return PlaceDataPack.NULL;
                } catch (ClassCastException ignore) {
                    return PlaceDataPack.NULL;
                }
                Block needBlock = bItem.getBlock();
                placeContext = bItem.updatePlacementContext(placeContext);
                if (placeContext == null) continue;
                //重新赋值 mojang在BlockItem L74这样写的
                if (!needBlock.isEnabled(placeContext.getLevel().enabledFeatures())) {
                    continue;
                }

                BlockState placementState = bItem.getPlacementState(placeContext);
                //更换世界后这边会失效 需要更新测试假人
                if (placementState == null) {
                    continue;
                }
                if (!MainDecide.INSTANCE.test(needState, placementState, placePos)) {
                    continue fV;
                }
                BooleanSupplier verify = () -> {
                    FakePlacementContext contextVerify = BlockStateVerify.getContextVerify(clickVec, placePos.relative(offDir), offDir.getOpposite(), stack, rData);
                    if (contextVerify.getClickedPos().equals(placePos) && contextVerify.getClickedFace().equals(offDir.getOpposite())) {
                        BlockState currentState = BlockStateVerify.genBlockState(contextVerify, needBlock);
                        return MainDecide.INSTANCE.test(needState, currentState, placePos);
                    }

                    return false;
                };

                return PlaceDataPack.plac(new PlaceData(placePos.relative(offDir), offDir.getOpposite(), clickVec, true, rData, verify));
            }
        }
        return PlaceDataPack.NULL;
    }

}
