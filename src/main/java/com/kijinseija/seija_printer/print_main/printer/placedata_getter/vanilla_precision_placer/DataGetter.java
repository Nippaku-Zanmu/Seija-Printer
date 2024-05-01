package com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import com.kijinseija.seija_printer.print_main.printer.util.BlockRotDataGetter;
import com.kijinseija.seija_printer.print_main.printer.util.BlockUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.*;

import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class DataGetter {
    //todo 活塞防止修复
    //todo airplace 非法转头支持
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Printer pri = Printer.getINSTANCE();
    public static Property[] check = new Property[]{
        Properties.FACING, Properties.HORIZONTAL_FACING, Properties.BLOCK_HALF
        , Properties.DOOR_HINGE, Properties.AXIS, Properties.ATTACHMENT
        , Properties.HOPPER_FACING, Properties.ROTATION, Properties.WALL_MOUNT_LOCATION
        , Properties.CHEST_TYPE, Properties.SLAB_TYPE
    };

    public static PlaceDataPack getData(BlockState needState, DirData data, ItemStack stack) {
        PlaceDataPack dataPla = getDataPla(needState, data, stack);
        if (dataPla.data().valid()){
            return dataPla;
        }
        return getDataInt(needState, data, stack);
    }
    private static PlaceDataPack getDataInt(BlockState needState, DirData d, ItemStack stack){
        DirDataI data = new DirDataI(d.placePos(),BlockUtil.getInteractDir(d.placePos()));
        BlockPos placePos = data.placePos();
        if (needState.getBlock().equals(mc.world.getBlockState(placePos).getBlock()))
            return PlaceDataPack.NULL;
        if (!canInte(placePos))
            return PlaceDataPack.NULL;
        RotationData rData = BlockRotDataGetter.getRotData(needState);
        fD:for (Direction offDir : data.dirs()) {
            fV:
            for (Vec3d clickVec : data.clickVecs(offDir)) {
//                if (mc.world.getBlockState(placePos).ca)

                FakePlacementContext placeContext = FakePlacementContext.getInstanceInte(clickVec, placePos, offDir, stack, rData);

                if (!placeContext.canReplaceExisting()){
                    continue fD;
                }

                BlockItem bItem = (BlockItem) needState.getBlock().asItem();
                Block needBlock = bItem.getBlock();
                if (!needBlock.isEnabled(placeContext.getWorld().getEnabledFeatures())) {
                    continue;
                }
                if (!placeContext.canPlace()) {
                    continue;
                }

                BlockState placementState = bItem.getPlacementState(placeContext);
                if (placementState == null) {
                    continue;
                }


                for (Property property : check) {
                    Comparable now = null;
                    Comparable need = null;
                    //todo 旗帜bug
                    try {
                        now = placementState.get(property);
                    } catch (IllegalArgumentException ignored) {
                    }
                    try {
                        need = needState.get(property);
                    } catch (IllegalArgumentException ignored) {
                    }

                    if (now != need) {
                        continue fV;
                    }
                }
                return PlaceDataPack.inte(new PlaceData(placePos, offDir, clickVec, true, rData));
            }
        }
        return PlaceDataPack.NULL;
    }
    private static boolean canInte(BlockPos pos){
        Block block = mc.world.getBlockState(pos).getBlock();
        if (((block instanceof AirBlock)||(block instanceof AbstractFireBlock))&&pri.airPlace.get()){
            return true;
        }
        if ((block instanceof FluidBlock)&&(pri.airPlace.get()||pri.liquidInt.get())){
            return true;
        }
        return !BlockUtil.canPlaceIn(pos);
    }
    private static PlaceDataPack getDataPla(BlockState needState, DirData data, ItemStack stack){
        BlockPos placePos = data.placePos();
        RotationData rData = BlockRotDataGetter.getRotData(needState);

        fD:for (Direction offDir : data.dirs()) {
            fV:
            for (Vec3d clickVec : data.clickVecs(offDir)) {


                FakePlacementContext placeContext = FakePlacementContext.getInstancePlac(clickVec, placePos, offDir, stack, rData);
                if (placeContext.canReplaceExisting()){
                    continue fD;
                }
                //                    if (placeContext.getBlockPos().equals(placePos)) {
//                        continue fD;
//                    }
                BlockItem bItem = (BlockItem) needState.getBlock().asItem();
                Block needBlock = bItem.getBlock();
                if (!needBlock.isEnabled(placeContext.getWorld().getEnabledFeatures())) {
                    continue;
                }
                if (!placeContext.canPlace()) {

                    continue;
                }

                BlockState placementState = bItem.getPlacementState(placeContext);
                if (placementState == null) {

                    continue;
                }


                for (Property property : check) {
                    Comparable now = null;
                    Comparable need = null;
                    //todo 旗帜bug
                    try {
                        now = placementState.get(property);
                    } catch (IllegalArgumentException ignored) {
                    }
                    try {
                        need = needState.get(property);
                    } catch (IllegalArgumentException ignored) {
                    }

                    if (now != need) {
                        continue fV;
                    }
                }
                return PlaceDataPack.plac(new PlaceData(placePos.offset(offDir), offDir.getOpposite(), clickVec, true, rData));
            }
        }
        return PlaceDataPack.NULL;
    }
}
