/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

//package com.kijinseija.seija_printer.print_main.printer.placedata_getter.getter;
//
//import com.kijinseija.seija_printer.print_main.printer.placedata_getter.AbstractDataGetter;
//import com.kijinseija.seija_printer.print_main.printer.util.BlockRotDataGetter;
//import com.kijinseija.seija_printer.print_main.printer.util.SeijaUtil;
//import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
//import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
//import com.kijinseija.seija_printer.print_main.printer.util.records.RotationData;
//import net.minecraft.block.*;
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.fluid.FluidState;
//import net.minecraft.fluid.Fluids;
//import net.minecraft.registry.tag.BlockTags;
//import net.minecraft.state.property.DirectionProperty;
//import net.minecraft.state.property.IntProperty;
//import net.minecraft.state.property.Properties;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Direction;
//import net.minecraft.util.math.RotationPropertyHelper;
//import net.minecraft.util.math.Vec3d;
//import net.minecraft.world.World;
//import net.minecraft.world.WorldView;
//
//import java.util.Optional;
//
//public class HangingSignDataGetter extends AbstractDataGetter {
//    private MinecraftClient mc = MinecraftClient.getInstance();
//    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
//    public static final IntProperty ROTATION = Properties.ROTATION;
//
//    @Override
//    public PlaceData getData(BlockState needState, DirData dirData) {
//        BlockPos placePos = dirData.placePos();
//
//        if (needState.getBlock() instanceof WallHangingSignBlock) {
//            Direction needDir = needState.get(FACING);
//            for (Direction offsetDir : dirData.dirs()) {
////                if (offsetDir.getAxis() == Direction.Axis.Y
////                    || needDir.getAxis() == offsetDir.getAxis()
////                    || !canAttachTo(placePos, offsetDir)) continue;
//                for (Vec3d clickVec : dirData.clickVecs(offsetDir)) {
//                    if (needDir == getFacDir(placePos, clickVec, offsetDir))
//                        return new PlaceData(placePos.offset(offsetDir), offsetDir.getOpposite(), clickVec, true, null);
//                }
//            }
//        } else {
//            RotationData rotData = BlockRotDataGetter.getRotData(needState);
//            for (Direction offsetDir : dirData.dirs()) {
//                if (offsetDir != Direction.UP
//                ) continue;
//                for (Vec3d clickVec : dirData.clickVecs(offsetDir)) {
//                    if (rotData!=null||getPlaceRotValue(placePos, clickVec, offsetDir) == needState.get(ROTATION))
//                        return new PlaceData(placePos.offset(offsetDir), offsetDir.getOpposite(), clickVec, true, rotData);
//                }
//            }
//        }
//        return PlaceData.NULL;
//    }
//
//    private int getPlaceRotValue(BlockPos placePos, Vec3d hitVec, Direction offdir) {
//        //修改自mc原版方法
//        boolean bl2;
//        FluidState fluidState = mc.world.getFluidState(placePos);
//        BlockPos blockPos = placePos.offset(offdir);
//        BlockState blockState = mc.world.getBlockState(blockPos);
//        boolean bl = blockState.isIn(BlockTags.ALL_HANGING_SIGNS);
//        Direction direction = Direction.fromRotation(SeijaUtil.getYaw(hitVec));
//        boolean bl3 = bl2 = !Block.isFaceFullSquare(blockState.getCollisionShape(mc.world, blockPos), Direction.DOWN) || SeijaUtil.isSneak();
//        if (bl && !SeijaUtil.isSneak()) {
//            Optional<Direction> optional;
//            if (blockState.contains(WallHangingSignBlock.FACING)) {
//                Direction direction2 = blockState.get(WallHangingSignBlock.FACING);
//                if (direction2.getAxis().test(direction)) {
//                    bl2 = false;
//                }
//            } else if (blockState.contains(ROTATION) && (optional = RotationPropertyHelper.toDirection(blockState.get(ROTATION))).isPresent() && optional.get().getAxis().test(direction)) {
//                bl2 = false;
//            }
//        }
//        return !bl2 ? RotationPropertyHelper.fromDirection(direction.getOpposite()) : RotationPropertyHelper.fromYaw((float) (SeijaUtil.getYaw(hitVec) + 180.0f));
//
//    }
//
//    private Direction getFacDir(BlockPos placePos, Vec3d clickVec, Direction offDir) {
//        //BlockState blockState = this.getDefaultState();
//        //FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
//        World worldView = mc.world;
//        BlockPos blockPos = placePos;
//        for (Direction direction : SeijaUtil.getPlacementDirections(clickVec,placePos,offDir)) {
//            Direction direction2;
//            if (direction.getAxis() == Direction.Axis.Y
//                || direction.getAxis().test(offDir.getOpposite())
//                || !canAttachTo(placePos, offDir)
//            ) continue;
//            return direction.getOpposite();
//        }
//        return null;
//    }
//
//    public boolean canAttachTo(BlockPos placePos, Direction offDir) {
//        BlockState blockState = mc.world.getBlockState(placePos.offset(offDir));
//        if (blockState.isIn(BlockTags.WALL_HANGING_SIGNS)) {
//            return blockState.get(FACING).getAxis() != offDir.getAxis();
//        }
//        return blockState.isSideSolid(mc.world, placePos.offset(offDir), offDir.getOpposite(), SideShapeType.FULL);
//    }
//
//    @Override
//    public boolean isSuitable(BlockState needState, BlockPos pos) {
//        return needState.getBlock() instanceof HangingSignBlock
//            || needState.getBlock() instanceof WallHangingSignBlock;
//    }
//}
