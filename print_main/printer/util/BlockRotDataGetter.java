/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.util;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import com.kijinseija.seija_printer.print_main.printer.util.records.RotationData;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.BigDripleafBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.DropperBlock;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.FlowerBedBlock;
import net.minecraft.world.level.block.GlazedTerracottaBlock;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.LoomBlock;
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.level.block.SmallDripleafBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.StonecutterBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;



public class BlockRotDataGetter {
    private static final Printer pri = Printer.getINSTANCE();

    /**
     * get vec
     * 获取强制放置指定方块状态的非法转头方向
     *
     * @param bs bs 需要的State
     * @return {@link RotationData}
     * @see RotationData
     */
    @Nullable
    public static RotationData getRotData(BlockState bs) {
        if (!pri.bSetIllegalRotate.get()) return null;//没开非法转头就结束
        Block block = bs.getBlock();
        //H rotate YClock
        //Anvil
        if (block instanceof AnvilBlock) {
            return getRotateDataFromDir(bs.getValue(BlockStateProperties.HORIZONTAL_FACING).getCounterClockWise());
        }
        //H opposite
        //Trapdoor Chest EnderChestBlock AbstractFurnaceBlock LecternBlock StonecutterBlock BeehiveBlock
        //RedStoneGate DripLeaf CarVedPumpkin ChiseledBookshelf EndProtalFra FlowerbedBlock
        //GlazedTerracottaBlock JigsawBlock LoomBlock
        if (block instanceof TrapDoorBlock || block instanceof ChestBlock || block instanceof EnderChestBlock
            || block instanceof AbstractFurnaceBlock || block instanceof LecternBlock || block instanceof StonecutterBlock
            || block instanceof BeehiveBlock || block instanceof DiodeBlock
            || block instanceof SmallDripleafBlock || block instanceof BigDripleafBlock
            || block instanceof CarvedPumpkinBlock || block instanceof ChiseledBookShelfBlock
            || block instanceof EndPortalFrameBlock || block instanceof FlowerBedBlock
            || block instanceof GlazedTerracottaBlock || block instanceof JigsawBlock
            || block instanceof LoomBlock || block instanceof ShelfBlock
        ) {
            return getRotateDataFromDir(bs.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite());
        }

        //H nor
        //Stair FenceGateBlock CalibratedSculkSensorBlock CampfireBlock Door Bed

        //rotate +=180
        //Sign HSign Banner
        if (block instanceof StandingSignBlock
            || block instanceof CeilingHangingSignBlock || block instanceof BannerBlock) {
            return getVecFromRotProp(bs.getValue(BlockStateProperties.ROTATION_16));
        }

        //rotate

        //Fac Oppo
        //PistonBlock DispenserBlock DropperBlock BarrelBlock CommandBlock
        if (block instanceof PistonBaseBlock || block instanceof DispenserBlock
            || block instanceof DropperBlock || block instanceof BarrelBlock
            || block instanceof CommandBlock) {
            return getRotateDataFromDir(bs.getValue(BlockStateProperties.FACING).getOpposite());
        }

        //Fac
        //observer


        if (bs.getProperties().contains(BlockStateProperties.FACING)) {
            return getRotateDataFromDir(bs.getValue(BlockStateProperties.FACING));
        }
        if (bs.getProperties().contains(BlockStateProperties.HORIZONTAL_FACING)) {
            return getRotateDataFromDir(bs.getValue(BlockStateProperties.HORIZONTAL_FACING));
        }
        if (bs.getProperties().contains(BlockStateProperties.ROTATION_16)) {
            return getVecFromRotProp2(bs.getValue(BlockStateProperties.ROTATION_16));
        }
        if (bs.getProperties().contains(BlockStateProperties.ORIENTATION)) {

            FrontAndTop ori = bs.getValue(BlockStateProperties.ORIENTATION);
            if (ori.front().getAxis() != Direction.Axis.Y)
                return getRotateDataFromDir(ori.front().getOpposite());
            return  RotationData.build(getRotateDataFromDir(ori.top().getOpposite()).yaw(), getRotateDataFromDir(ori.front().getOpposite()).pitch());
        }

        return null;
    }

    public static RotationData getRotateDataFromDir(Direction dir) {
        RotationData data =  RotationData.build(0, 0);
        switch (dir) {
            case EAST -> data =  RotationData.build(-90, 0);
            case WEST -> data =  RotationData.build(90, 0);

            case NORTH -> data =  RotationData.build(180, 0);

            case SOUTH -> data =  RotationData.build(0, 0);

            case DOWN -> data =  RotationData.build(0, 90);

            case UP -> data =  RotationData.build(0, -90);

        }
        return data;
    }

    public static RotationData getVecFromRotProp(int prop) {
        return  RotationData.build(Mth.wrapDegrees(22.5 * prop - 180), 0);
    }

    public static RotationData getVecFromRotProp2(int prop) {
        return  RotationData.build(Mth.wrapDegrees(22.5 * prop), 0);
    }
}
