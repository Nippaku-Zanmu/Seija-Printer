/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

//package com.kijinseija.seija_printer.print_main.printer.placedata_getter.getter;
//
//import com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer.FakePlacementContext;
//import meteordevelopment.meteorclient.utils.player.ChatUtils;
//import net.minecraft.block.Block;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.FluidFillable;
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.fluid.FlowableFluid;
//import net.minecraft.fluid.Fluid;
//import net.minecraft.fluid.Fluids;
//import net.minecraft.item.BucketItem;
//import net.minecraft.item.ItemStack;
//import net.minecraft.registry.tag.FluidTags;
//import net.minecraft.text.Text;
//import net.minecraft.util.hit.BlockHitResult;
//import net.minecraft.util.hit.HitResult;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Direction;
//import net.minecraft.util.math.Vec3d;
//import net.minecraft.world.RaycastContext;
//import org.jetbrains.annotations.Nullable;
//
//public class LiquidHelper {
//    private static final MinecraftClient mc = MinecraftClient.getInstance();
//
//    protected static BlockHitResult raycast(RaycastContext.FluidHandling fluidHandling) {
//        PlayerEntity player = FakePlacementContext.getFakePlayer();
//        ChatUtils.sendMsg(Text.of("334"));
//        Vec3d vec3d = player.getEyePos();
//        Vec3d vec3d2 = vec3d.add(player.getRotationVector(player.getPitch(), player.getYaw()).multiply(player.getBlockInteractionRange()));
//        ChatUtils.sendMsg(Text.of(player.getYaw()+",P"+player.getPitch()));
//        ChatUtils.sendMsg(Text.of(""+ player.getEyePos()));
//        return mc.world.raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.OUTLINE, fluidHandling, player));
//    }
//
//    public static boolean canPlaceLiquid(ItemStack stack, Vec3d clickVec, BlockPos placePos, Direction offsetDir) {
//        if (!(stack.getItem() instanceof BucketItem)) return false;
//        Fluid fluid = ((BucketItem) stack.getItem()).fluid;
//        FakePlacementContext.getInstancePlac(clickVec, placePos, offsetDir, stack);
//        PlayerEntity player = FakePlacementContext.getFakePlayer();
//        Vec3d vec3d = player.getEyePos();
//        ChatUtils.sendMsg(Text.of(player.getYaw()+",P"+player.getPitch()));
//        ChatUtils.sendMsg(Text.of(""+ vec3d));
//        BlockHitResult blockHitResult = raycast(RaycastContext.FluidHandling.NONE);
//        if (blockHitResult.getType() == HitResult.Type.MISS) {
//            ChatUtils.sendMsg(Text.of("Return1"));
//            return false;
////            return TypedActionResult.pass(itemStack);
//        }
//        if (blockHitResult.getType() == HitResult.Type.BLOCK) {
//            BlockPos blockPos3;
//            BlockPos blockPos = blockHitResult.getBlockPos();
//            Direction direction = blockHitResult.getSide();
//            BlockPos blockPos2 = blockPos.offset(direction);
//
//            BlockState blockState = mc.world.getBlockState(blockPos);
//            BlockPos blockPos4 = blockPos3 = blockState.getBlock() instanceof FluidFillable && fluid == Fluids.WATER ? blockPos : blockPos2;
//            if (placeFluid(blockPos3, stack, blockHitResult)) {
//                return blockPos3.equals(placePos);
//                //  return TypedActionResult.success(itemStack2, world.isClient());
//            }
////            return TypedActionResult.fail(itemStack);
//        }
////        return TypedActionResult.pass(itemStack);
//        ChatUtils.sendMsg(Text.of("Return2"));
//        return false;
//    }
//
//    public static boolean placeFluid(BlockPos pos, ItemStack stack, @Nullable BlockHitResult hitResult) {
//        PlayerEntity player = FakePlacementContext.getFakePlayer();
//        FluidFillable fluidFillable;
//        boolean bl2;
//        if (!(stack.getItem() instanceof BucketItem)) return false;
//        Fluid fluid = ((BucketItem) stack.getItem()).fluid;
//        if (!(fluid instanceof FlowableFluid)) {
//            ChatUtils.sendMsg(Text.of("Return3"));
//            return false;
//        }
//        FlowableFluid flowableFluid = (FlowableFluid) fluid;
//        BlockState blockState = mc.world.getBlockState(pos);
//        Block block = blockState.getBlock();
//        boolean bl = blockState.canBucketPlace(fluid);
//        boolean bl3 = bl2 = blockState.isAir() || bl || block instanceof FluidFillable && (fluidFillable = (FluidFillable) ((Object) block)).canFillWithFluid(player, mc.world, pos, blockState, fluid);
//        if (!bl2) {
//            return hitResult != null && placeFluid(hitResult.getBlockPos().offset(hitResult.getSide()), stack, null);
//        }
//        if (mc.world.getDimension().ultrawarm() && fluid.isIn(FluidTags.WATER)) {
//            int i = pos.getX();
//            int j = pos.getY();
//            int k = pos.getZ();
//            return true;
//        }
//        if (block instanceof FluidFillable) {
//            fluidFillable = (FluidFillable) ((Object) block);
//            if (fluid == Fluids.WATER) {
//
//                return true;
//            }
//        }
//
//        if (blockState.getFluidState().isStill()) {
//            return true;
//        }
//        ChatUtils.sendMsg(Text.of("Return4"));
//        return false;
//    }
//}
