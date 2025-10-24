package com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer;

import com.kijinseija.seija_printer.print_main.printer.util.BlockUtil;
import com.kijinseija.seija_printer.print_main.printer.util.PredictUtility;
import com.kijinseija.seija_printer.print_main.printer.util.RayTraceUtil;
import com.kijinseija.seija_printer.print_main.printer.util.SeijaUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.RotationData;
import com.mojang.authlib.GameProfile;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.util.UUID;

public class FakePlacementContext extends ItemPlacementContext {


    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static PlayerEntity getFakePlayer() {
        return fakePlayer;
    }

    protected static PlayerEntity fakePlayer;

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
        fakePlayer = new PlayerEntity(mc.world, new GameProfile(UUID.fromString("66123666-1234-5432-6666-667563866600"), "PredictEntity339")) {



            @Override
            public void tick() {

            }

            @Override
            public @NotNull GameMode getGameMode() {
                return GameMode.SURVIVAL;
            }

        };
    }

    protected static void setRotate(PlayerEntity e, float yaw, float pitch) {
        e.setYaw(yaw);
        e.setPitch(pitch);
        e.setHeadYaw(yaw);
        e.setBodyYaw(yaw);

        e.lastHeadYaw = yaw;
        e.lastYaw = yaw;
        e.lastPitch = pitch;

    }
    protected static void setMovementMode(PlayerEntity e){
        e.setSwimming(mc.player.isSwimming());
        if (mc.player.isGliding()) {
            e.startGliding();
        }else e.stopGliding();
    }

    public static FakePlacementContext getInstanceInte(Vec3d clickVec, BlockPos placePos, Direction offsetDir, ItemStack stack) {
        BlockHitResult hitRes = BlockUtil.getHitRes(placePos, offsetDir, clickVec);
        fakePlayer.setPosition(PredictUtility.getPredPlayerVec());
        setRotate(fakePlayer,(float) SeijaUtil.getYaw(clickVec),(float) SeijaUtil.getPitch(clickVec));
        setMovementMode(fakePlayer);
        fakePlayer.setSneaking(SeijaUtil.isSneak());
        return new FakePlacementContext(fakePlayer, Hand.MAIN_HAND, stack, hitRes);
    }

    public static FakePlacementContext getInstanceInte(Vec3d clickVec, BlockPos placePos, Direction offsetDir, ItemStack stack, @Nullable RotationData rdata) {
        if (rdata == null) return getInstanceInte(clickVec, placePos, offsetDir, stack);
        BlockHitResult hitRes = BlockUtil.getHitRes(placePos, offsetDir, clickVec);
        fakePlayer.setPosition(PredictUtility.getPredPlayerVec());
        setRotate(fakePlayer,(float) rdata.yaw(),(float) rdata.pitch());
        setMovementMode(fakePlayer);
        fakePlayer.setSneaking(SeijaUtil.isSneak());
        return new FakePlacementContext(fakePlayer, Hand.MAIN_HAND, stack, hitRes);
    }

    public static FakePlacementContext getInstancePlac(Vec3d clickVec, BlockPos placePos, Direction offsetDir, ItemStack stack) {
        return getInstanceInte(clickVec, placePos.offset(offsetDir), offsetDir.getOpposite(), stack);

//        BlockHitResult hitRes = BlockUtil.getHitRes(placePos.offset(offsetDir), offsetDir.getOpposite(), clickVec);
//        fakePlayer.setPosition(PredictUtility.getPredPlayerVec());
//        fakePlayer.setYaw((float) SeijaUtil.getYaw(clickVec));
//        fakePlayer.setPitch((float) SeijaUtil.getPitch(clickVec));
//        fakePlayer.setSneaking(SeijaUtil.isSneak());
//        return new FakePlacementContext(fakePlayer,Hand.MAIN_HAND,stack,hitRes);
    }

    //方便使用
    //直接拿get到的方向和要填充方块的坐标带入即可
    public static FakePlacementContext getInstancePlac(Vec3d clickVec, BlockPos placePos, Direction offsetDir, ItemStack stack, @Nullable RotationData rdata) {
        return getInstanceInte(clickVec, placePos.offset(offsetDir), offsetDir.getOpposite(), stack, rdata);
    }



    public FakePlacementContext(PlayerEntity player, Hand hand, ItemStack stack, BlockHitResult hitResult) {
        super(player, hand, stack, hitResult);
        //updatePlayerEntity();
    }

    @Override
    public String toString() {
        return "Vec: " + getHitResult().getPos() + " Block: " + getHitResult().getBlockPos()
            + " Dir: " + getHitResult().getSide();
    }
}
