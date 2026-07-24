/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import com.kijinseija.seija_printer.print_main.printer.util.*;
import com.kijinseija.seija_printer.print_main.printer.util.records.RotationData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;


import static com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer.FakePlacementContext.fakePlayer;
import static com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer.FakePlacementContext.setRotate;


public class BlockStateVerify {
    private static final Minecraft mc = Minecraft.getInstance();

    private static Printer pri() {
        return Printer.getINSTANCE();
    }
    public static float sendYaw,sendPitch=0;

    protected static FakePlacementContext getContextVerify(Vec3 clickVec, BlockPos placePos, Direction offsetDir, ItemStack stack, @Nullable RotationData rdata) {

        // Fabric has no global outgoing-packet event. The vanilla player state
        // is the authoritative rotation immediately before placement.
        if (mc.player != null) {
            sendYaw = mc.player.getYRot();
            sendPitch = mc.player.getXRot();
        }

        fakePlayer.setPos(mc.player.position());
        if (rdata == null) {
            setRotate(fakePlayer, sendYaw, sendPitch);
            //ChatUtils.sendMsg(Text.of(mc.player.getYaw() + "," + mc.player.getPitch()));
        } else {
            setRotate(fakePlayer, (float) rdata.yaw(), (float) rdata.pitch());
        }
        rdata = RotationData.build(sendYaw,sendPitch);

        FakePlacementContext.setMovementMode(fakePlayer);
        fakePlayer.setShiftKeyDown(SeijaUtil.isSneak());


        BlockHitResult hitRes = (pri().bSetRayTrace.get() && pri().bSetRayTrace.isVisible()) ? RayTraceUtil.INSTANCE.rayHitRes(fakePlayer.getEyePosition(), rdata, pri().bSetIgnoreEntity.get(), pri().dSetPrintingRange.get())
            : BlockUtil.getHitRes(placePos, offsetDir, clickVec);

        return new FakePlacementContext(fakePlayer, InteractionHand.MAIN_HAND, stack, hitRes);
    }

    //用于后检测
    protected static BlockState genBlockState(BlockPlaceContext placeContext, Block b) {

        if (!placeContext.canPlace()) {
            return null;
        }
        BlockItem bItem;
        try {
            if (!(InvUtil.getItemFormBlock(b) instanceof BlockItem item)) return null;
            bItem = item;
            if (bItem == null) return null;
        } catch (ClassCastException ignore) {
            return null;
        }
        Block needBlock = bItem.getBlock();
        placeContext = bItem.updatePlacementContext(placeContext);
        if (placeContext == null) return null;
        if (!needBlock.isEnabled(placeContext.getLevel().enabledFeatures())) {
            return null;
        }
        if (!placeContext.canPlace()) {
            return null;
        }//test
        return bItem.getPlacementState(placeContext);
    }
}
