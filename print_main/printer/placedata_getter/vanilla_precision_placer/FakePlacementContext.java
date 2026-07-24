/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer;

import com.kijinseija.seija_printer.print_main.printer.util.BlockUtil;
import com.kijinseija.seija_printer.print_main.printer.util.PredictUtility;
import com.kijinseija.seija_printer.print_main.printer.util.RayTraceUtil;
import com.kijinseija.seija_printer.print_main.printer.util.SeijaUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.RotationData;
import com.mojang.authlib.GameProfile;
import com.kijinseija.seija_printer.utils.player.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.util.UUID;

public class FakePlacementContext extends BlockPlaceContext {


    private static final Minecraft mc = Minecraft.getInstance();

    public static Player getFakePlayer() {
        return fakePlayer;
    }

    protected static Player fakePlayer;

    //        ;= new PlayerEntity(mc.world, BlockPos.ORIGIN, 1, new GameProfile(UUID.fromString("66123666-1234-5432-6666-667563866600"), "PredictEntity339")){
//
//        public boolean isSpectator() {
//            return false;
//        }
//
//        public boolean isCreative() {
//            return false;
//        }
//    };
    public static void updatePlayerEntity() {
        if (mc.level == null) {
            fakePlayer = null;
            return;
        }
        fakePlayer = new Player(mc.level, new GameProfile(UUID.fromString("66123666-1234-5432-6666-667563866600"), "PredictEntity339")) {



            @Override
            public void tick() {

            }

            @Override
            public @NotNull GameType gameMode() {
                return GameType.SURVIVAL;
            }

        };
    }

    protected static void setRotate(Player e, float yaw, float pitch) {
        e.setYRot(yaw);
        e.setXRot(pitch);
        e.setYHeadRot(yaw);
        e.setYBodyRot(yaw);

        e.yHeadRotO = yaw;
        e.yRotO = yaw;
        e.xRotO = pitch;

    }
    protected static void setMovementMode(Player e){
        e.setSwimming(mc.player.isSwimming());
        if (mc.player.isFallFlying()) {
            e.startFallFlying();
        }else e.stopFallFlying();
    }

    public static FakePlacementContext getInstanceInte(Vec3 clickVec, BlockPos placePos, Direction offsetDir, ItemStack stack) {
        BlockHitResult hitRes = BlockUtil.getHitRes(placePos, offsetDir, clickVec);
        fakePlayer.setPos(PredictUtility.getPredPlayerVec());
        setRotate(fakePlayer,(float) SeijaUtil.getYaw(clickVec),(float) SeijaUtil.getPitch(clickVec));
        setMovementMode(fakePlayer);
        fakePlayer.setShiftKeyDown(SeijaUtil.isSneak());
        return new FakePlacementContext(fakePlayer, InteractionHand.MAIN_HAND, stack, hitRes);
    }

    public static FakePlacementContext getInstanceInte(Vec3 clickVec, BlockPos placePos, Direction offsetDir, ItemStack stack, @Nullable RotationData rdata) {
        if (rdata == null) return getInstanceInte(clickVec, placePos, offsetDir, stack);
        BlockHitResult hitRes = BlockUtil.getHitRes(placePos, offsetDir, clickVec);
        fakePlayer.setPos(PredictUtility.getPredPlayerVec());
        setRotate(fakePlayer,(float) rdata.yaw(),(float) rdata.pitch());
        setMovementMode(fakePlayer);
        fakePlayer.setShiftKeyDown(SeijaUtil.isSneak());
        return new FakePlacementContext(fakePlayer, InteractionHand.MAIN_HAND, stack, hitRes);
    }

    public static FakePlacementContext getInstancePlac(Vec3 clickVec, BlockPos placePos, Direction offsetDir, ItemStack stack) {
        return getInstanceInte(clickVec, placePos.relative(offsetDir), offsetDir.getOpposite(), stack);

//        BlockHitResult hitRes = BlockUtil.getHitRes(placePos.offset(offsetDir), offsetDir.getOpposite(), clickVec);
//        fakePlayer.setPosition(PredictUtility.getPredPlayerVec());
//        fakePlayer.setYaw((float) SeijaUtil.getYaw(clickVec));
//        fakePlayer.setPitch((float) SeijaUtil.getPitch(clickVec));
//        fakePlayer.setSneaking(SeijaUtil.isSneak());
//        return new FakePlacementContext(fakePlayer,Hand.MAIN_HAND,stack,hitRes);
    }

    //方便使用
    //直接拿get到的方向和要填充方块的坐标带入即可
    public static FakePlacementContext getInstancePlac(Vec3 clickVec, BlockPos placePos, Direction offsetDir, ItemStack stack, @Nullable RotationData rdata) {
        return getInstanceInte(clickVec, placePos.relative(offsetDir), offsetDir.getOpposite(), stack, rdata);
    }



    public FakePlacementContext(Player player, InteractionHand hand, ItemStack stack, BlockHitResult hitResult) {
        super(player, hand, stack, hitResult);
        //updatePlayerEntity();
    }

    @Override
    public String toString() {
        return "Vec: " + getHitResult().getLocation() + " Block: " + getHitResult().getBlockPos()
            + " Dir: " + getHitResult().getDirection();
    }
}
