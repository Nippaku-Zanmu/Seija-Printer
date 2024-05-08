package com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer.state_decide.MainDecide;
import com.kijinseija.seija_printer.print_main.printer.util.BlockRotDataGetter;
import com.kijinseija.seija_printer.print_main.printer.util.BlockUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.*;

import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class DataGetter {

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Printer pri = Printer.getINSTANCE();
//    public static Property[] check = new Property[]{
//        Properties.FACING, Properties.HORIZONTAL_FACING, Properties.BLOCK_HALF
//        , Properties.DOOR_HINGE, Properties.AXIS, Properties.ATTACHMENT
//        , Properties.HOPPER_FACING, Properties.ROTATION, Properties.WALL_MOUNT_LOCATION
//        , Properties.CHEST_TYPE, Properties.SLAB_TYPE
//    };

    public static PlaceDataPack getData(BlockState needState, DirData data, ItemStack stack) {
        PlaceDataPack dataPla = getDataPla(needState, data, stack);
      //  ChatUtils.sendMsg(Text.of("getDataPla"));
        if (dataPla.data().valid()){
         //  ChatUtils.sendMsg(Text.of("retData"));
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


                if (!mc.world.getBlockState(data.placePos()).canReplace(placeContext)){
                    // ChatUtils.sendMsg(Text.of("cont1"));
                    continue fD;
                }

                BlockItem bItem = (BlockItem) needState.getBlock().asItem();
                Block needBlock = bItem.getBlock();
                if (!needBlock.isEnabled(placeContext.getWorld().getEnabledFeatures())) {
                    continue;
                }
//                if (!placeContext.canPlace()) {
//                    continue;
//                }

                BlockState placementState = bItem.getPlacementState(placeContext);
                if (placementState == null) {
                    continue;
                }

                if (!MainDecide.INSTANCE.test(needState,placementState,placePos)){
                    continue fV;
                }
//                for (Property property : check) {
//                    Comparable now = null;
//                    Comparable need = null;
//                    try {
//                        now = placementState.get(property);
//                    } catch (IllegalArgumentException ignored) {
//                    }
//                    try {
//                        need = needState.get(property);
//                    } catch (IllegalArgumentException ignored) {
//                    }
//
//                    if (now != need) {
//                        continue fV;
//                    }
//                }
                return PlaceDataPack.inte(new PlaceData(placePos, offDir, clickVec, true, rData));
            }
        }
        return PlaceDataPack.NULL;
    }
    private static boolean canInte(BlockPos pos){
        Block block = mc.world.getBlockState(pos).getBlock();
        if (((block instanceof AirBlock)||(block instanceof AbstractFireBlock))&&pri.bSetAirPlace.get()){
            return true;
        }
        if ((block instanceof FluidBlock)&&(pri.bSetAirPlace.get()||pri.bSetLiquidInt.get())){
            return true;
        }
        return !BlockUtil.canPlaceIn(pos);
    }
    private static PlaceDataPack getDataPla(BlockState needState, DirData data, ItemStack stack){
        BlockPos placePos = data.placePos();
        RotationData rData = BlockRotDataGetter.getRotData(needState);
        if (needState.getBlock().equals(mc.world.getBlockState(placePos).getBlock()))
            return PlaceDataPack.NULL;
        fD:for (Direction offDir : data.dirs()) {
            fV:
            for (Vec3d clickVec : data.clickVecs(offDir)) {


                FakePlacementContext placeContext = FakePlacementContext.getInstancePlac(clickVec, placePos, offDir, stack, rData);
                if (mc.world.getBlockState(data.placePos().offset(offDir)).canReplace(placeContext)){
                  // ChatUtils.sendMsg(Text.of("cont1"));
                    continue fD;
                }

                BlockItem bItem = (BlockItem) needState.getBlock().asItem();
                Block needBlock = bItem.getBlock();
                if (!needBlock.isEnabled(placeContext.getWorld().getEnabledFeatures())) {
                   //ChatUtils.sendMsg(Text.of("cont2"));
                    continue;
                }
//                if (!placeContext.canPlace()) {
//                   ChatUtils.sendMsg(Text.of("cont3"));
//                    continue;
//                }

                BlockState placementState = bItem.getPlacementState(placeContext);

                if (placementState == null) {
                   // ChatUtils.sendMsg(Text.of("cont4"));
                    continue;
                }
                if (!MainDecide.INSTANCE.test(needState,placementState,placePos))continue fV;
//
//
//                for (Property property : check) {
//                    Comparable now = null;
//                    Comparable need = null;
//
//                    try {
//                        now = placementState.get(property);
//                    } catch (IllegalArgumentException ignored) {
//                    }
//                    try {
//                        need = needState.get(property);
//                    } catch (IllegalArgumentException ignored) {
//                    }
//
//                    if (now != need) {
//                       // ChatUtils.sendMsg(Text.of("cont5"));
//                        continue fV;
//                    }
//                }
                return PlaceDataPack.plac(new PlaceData(placePos.offset(offDir), offDir.getOpposite(), clickVec, true, rData));
            }
        }
        return PlaceDataPack.NULL;
    }
}
