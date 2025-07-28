package com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer.state_decide.MainDecide;
import com.kijinseija.seija_printer.print_main.printer.util.BlockRotDataGetter;
import com.kijinseija.seija_printer.print_main.printer.util.BlockUtil;
import com.kijinseija.seija_printer.print_main.printer.util.InvUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.*;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class DataGetter {

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Printer pri = Printer.getINSTANCE();

    public static PlaceDataPack getData(BlockState needState, DirData data, ItemStack stack) {
        if (needState.getBlock() instanceof FluidBlock) return PlaceDataPack.NULL;

        PlaceDataPack dataPla = getd(needState, data, stack,false);
        if (dataPla.data().valid()) {
            return dataPla;
        }

        return getd(needState, data, stack,true);
    }

<<<<<<< Updated upstream
    private static PlaceDataPack getDataInt(BlockState needState, DirData d, ItemStack stack) {
        DirDataI data = new DirDataI(d.placePos(), BlockUtil.getInteractDir(d.placePos()));
=======
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
        if (mode)data = new DirData(data.placePos(), BlockUtil.getInteractDir(data.placePos()));
        BlockPos placePos = data.placePos();


        if (needState.getBlock().equals(mc.world.getBlockState(placePos).getBlock()))
            return PlaceDataPack.NULL;

        if (mode && !canInte(placePos)) {
            return PlaceDataPack.NULL;
        }
        RotationData rData = BlockRotDataGetter.getRotData(needState);
        fD:
        for (Direction offDir : data.dirs()) {
            fV:
            for (Vec3d clickVec : mode ? data.clickVecsInte(offDir) : data.clickVecs(offDir)) {
                ItemPlacementContext placeContext = mode ? FakePlacementContext.getInstanceInte(clickVec, placePos, offDir, stack, rData)
                    : FakePlacementContext.getInstancePlac(clickVec, placePos, offDir, stack, rData);

                if (!mode && !mc.world.getBlockState(data.placePos()).canReplace(placeContext)) {

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
                placeContext = bItem.getPlacementContext(placeContext);
                if (placeContext == null) {

                    continue;
                };
                if (!needBlock.isEnabled(placeContext.getWorld().getEnabledFeatures())) {

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
                    FakePlacementContext contextVerify = BlockStateVerify.getContextVerify(clickVec, mode ? placePos : placePos.offset(offDir), mode ? offDir : offDir.getOpposite(), stack, rData);
                    if (contextVerify.getBlockPos().equals(placePos) && contextVerify.getSide().equals(mode ? offDir : offDir.getOpposite())) {
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
                    return PlaceDataPack.plac(new PlaceData(placePos.offset(offDir), offDir.getOpposite(), clickVec, true, rData, verify));
            }
        }

        return PlaceDataPack.NULL;
    }

    private static PlaceDataPack getDataInt(BlockState needState, DirData data, ItemStack stack) {
>>>>>>> Stashed changes
        BlockPos placePos = data.placePos();
        if (needState.getBlock().equals(mc.world.getBlockState(placePos).getBlock())) {
            return PlaceDataPack.NULL;
        }
        if (!canInte(placePos)) {
            return PlaceDataPack.NULL;
        }

        RotationData rData = BlockRotDataGetter.getRotData(needState);
        fD:
        for (Direction offDir : data.dirs()) {
            fV:
            for (Vec3d clickVec : data.clickVecs(offDir)) {
                ItemPlacementContext placeContext = FakePlacementContext.getInstanceInte(clickVec, placePos, offDir, stack, rData);

                if (!mc.world.getBlockState(data.placePos()).canReplace(placeContext)) {
                    continue fD;
                }
                BlockItem bItem;
                try {
                    bItem = (BlockItem) InvUtil.getItemFormBlock(needState.getBlock());
                    if (bItem == null) return PlaceDataPack.NULL;
                } catch (ClassCastException ignore) {
                    return PlaceDataPack.NULL;
                }
                Block needBlock = bItem.getBlock();
                placeContext = bItem.getPlacementContext(placeContext);
                //重新赋值 mojang在BlockItem L74这样写的
                if (placeContext == null) continue;
                if (!needBlock.isEnabled(placeContext.getWorld().getEnabledFeatures())) {
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
<<<<<<< Updated upstream
                return PlaceDataPack.inte(new PlaceData(placePos, offDir, clickVec, true, rData));
=======
                BooleanSupplier verify = () -> {
                    FakePlacementContext contextVerify = BlockStateVerify.getContextVerify(clickVec, placePos, offDir, stack, rData);
                    if (contextVerify.getBlockPos().equals(placePos) && contextVerify.getSide().equals(offDir)) {
                        BlockState currentState = BlockStateVerify.genBlockState(contextVerify, needBlock);
                        return MainDecide.INSTANCE.test(needState, currentState, placePos);
                    }
                    ChatUtils.sendMsg(Text.of("RDir " + contextVerify.getSide() + "TDir: " + offDir.getOpposite()));
                    ChatUtils.sendMsg(Text.of("RPos " + contextVerify.getBlockPos() + "TPos: " + placePos.offset(offDir)));
                    return false;
                };
                return PlaceDataPack.inte(new PlaceData(placePos, offDir, clickVec, true, rData, verify));
>>>>>>> Stashed changes
            }
        }
        return PlaceDataPack.NULL;
    }

    private static boolean canInte(BlockPos pos) {
        Block block = mc.world.getBlockState(pos).getBlock();
        if (((block instanceof AirBlock) || (block instanceof AbstractFireBlock)) && pri.bSetAirPlace.get()) {
            return true;
        }
        if ((block instanceof FluidBlock) && (pri.bSetAirPlace.get() || pri.bSetLiquidInt.get())) {
            return true;
        }
        return (!BlockUtil.canPlaceIn(pos)) || block instanceof SlabBlock;
    }

    private static PlaceDataPack getDataPla(BlockState needState, DirData data, ItemStack stack) {
        BlockPos placePos = data.placePos();


        if (needState.getBlock().equals(mc.world.getBlockState(placePos).getBlock()))
            return PlaceDataPack.NULL;
        RotationData rData = BlockRotDataGetter.getRotData(needState);
        fD:
        for (Direction offDir : data.dirs()) {
            fV:
            for (Vec3d clickVec : data.clickVecs(offDir)) {
                ItemPlacementContext placeContext = FakePlacementContext.getInstancePlac(clickVec, placePos, offDir, stack, rData);

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
                placeContext = bItem.getPlacementContext(placeContext);
                if (placeContext == null) continue;
                //重新赋值 mojang在BlockItem L74这样写的
                if (!needBlock.isEnabled(placeContext.getWorld().getEnabledFeatures())) {
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
<<<<<<< Updated upstream
                return PlaceDataPack.plac(new PlaceData(placePos.offset(offDir), offDir.getOpposite(), clickVec, true, rData));
=======
                BooleanSupplier verify = () -> {
                    FakePlacementContext contextVerify = BlockStateVerify.getContextVerify(clickVec, placePos.offset(offDir), offDir.getOpposite(), stack, rData);
                    if (contextVerify.getBlockPos().equals(placePos) && contextVerify.getSide().equals(offDir.getOpposite())) {
                        BlockState currentState = BlockStateVerify.genBlockState(contextVerify, needBlock);
                        return MainDecide.INSTANCE.test(needState, currentState, placePos);
                    }

                    return false;
                };

                return PlaceDataPack.plac(new PlaceData(placePos.offset(offDir), offDir.getOpposite(), clickVec, true, rData, verify));
>>>>>>> Stashed changes
            }
        }
        return PlaceDataPack.NULL;
    }
}
