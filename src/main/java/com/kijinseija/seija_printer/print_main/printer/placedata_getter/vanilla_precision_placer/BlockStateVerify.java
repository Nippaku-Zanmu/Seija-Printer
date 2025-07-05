package com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import com.kijinseija.seija_printer.print_main.printer.util.*;
import com.kijinseija.seija_printer.print_main.printer.util.records.RotationData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

import static com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer.FakePlacementContext.fakePlayer;
import static com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer.FakePlacementContext.setRotate;


public class BlockStateVerify {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private static Printer pri() {
        return Printer.getINSTANCE();
    }

    protected static FakePlacementContext getContextVerify(Vec3d clickVec, BlockPos placePos, Direction offsetDir, ItemStack stack, @Nullable RotationData rdata) {

        fakePlayer.setPosition(mc.player.getPos());
        if (rdata == null) {
            setRotate(fakePlayer, mc.player.getYaw(), mc.player.getPitch());
        } else {
            setRotate(fakePlayer, (float) rdata.yaw(), (float) rdata.pitch());
        }
        rdata = new RotationData( mc.player.getYaw(), mc.player.getPitch());
        fakePlayer.setSwimming(mc.player.isSwimming());
        if (mc.player.isGliding()) {
            fakePlayer.startGliding();
        }else fakePlayer.stopGliding();
        fakePlayer.setSneaking(SeijaUtil.isSneak());

        BlockHitResult hitRes =pri().bSetRayTrace.get()? RayTraceUtil.INSTANCE.rayHitRes(fakePlayer.getEyePos(), rdata, pri().bSetIgnoreEntity.get(), pri().dSetPrintingRange.get())
            : BlockUtil.getHitRes(placePos, offsetDir, clickVec);;
        return new FakePlacementContext(fakePlayer, Hand.MAIN_HAND, stack, hitRes);
    }
    //用于后检测
    protected static BlockState genBlockState(ItemPlacementContext placeContext,Block b){

        if (!placeContext.canPlace()) {
            return null;
        }
        BlockItem bItem;
        try {
            bItem = (BlockItem) InvUtil.getItemFormBlock(b);
            if (bItem==null)return null;
        } catch (ClassCastException ignore) {
            return null;
        }
        Block needBlock = bItem.getBlock();
        placeContext = bItem.getPlacementContext(placeContext);
        if (placeContext == null) return null;
        if (!needBlock.isEnabled(placeContext.getWorld().getEnabledFeatures())) {
            return null;
        }
        if (!placeContext.canPlace()) {
            return null;
        }//test
        return bItem.getPlacementState(placeContext);
    }
}
