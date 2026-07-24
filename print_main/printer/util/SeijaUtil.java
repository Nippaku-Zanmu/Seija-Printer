/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.util;

import com.kijinseija.seija_printer.mixin.ClientWorldAccessor;
import com.kijinseija.seija_printer.print_main.modules.Printer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.prediction.BlockStatePredictionHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;

public class SeijaUtil {
    public static Printer pri = Printer.getINSTANCE();
    static Minecraft mc = Minecraft.getInstance();

    public static boolean isSneak(){
        return mc.player.isShiftKeyDown()||pri.bSetSneak.get();
    }

    public static double getEyeHeight() {
        double eyeHeight;
        if (pri.bSetSneak.get()) {

            if (mc.player.getEyeHeight(mc.player.getPose()) < 1) {
                eyeHeight = mc.player.getEyeHeight(mc.player.getPose());
            } else eyeHeight = mc.player.getEyeHeight(Pose.CROUCHING);
        } else
            eyeHeight = mc.player.getEyePosition().y - mc.player.position().y;
        return eyeHeight;
    }

    public static double getYaw(Vec3 pos) {

        Vec3 pVec = PredictUtility.getPredPlayerVec();
        return mc.player.getYRot() + Mth.wrapDegrees((float) Math.toDegrees(Math.atan2(pos.z() - pVec.z(), pos.x() - pVec.x())) - 90f - mc.player.getYRot());
    }

    public static double getPitch(Vec3 pos) {
        Vec3 pVec = PredictUtility.getPredPlayerVec();
        double eyeHeight = getEyeHeight();


        double diffX = pos.x() - pVec.x();
        double diffY = pos.y() - (pVec.y() + eyeHeight);
        double diffZ = pos.z() - pVec.z();

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        return mc.player.getXRot() + Mth.wrapDegrees((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)) - mc.player.getXRot());
    }

    public static int getSequence() {
        BlockStatePredictionHandler sequence = ((ClientWorldAccessor) mc.level).getPendingUpdateManager().startPredicting();
        return sequence.currentSequence();
    }


    public static double distanceBetween(BlockPos pos1, BlockPos pos2) {
        double d = pos1.getX() - pos2.getX();
        double e = pos1.getY() - pos2.getY();
        double f = pos1.getZ() - pos2.getZ();
        return Mth.sqrt((float) (d * d + e * e + f * f));
    }

    public static Direction[] getEntityFacingOrder(float yaw, float pitch) {
        Direction direction3;
        float f = pitch * ((float) Math.PI / 180);
        float g = -yaw * ((float) Math.PI / 180);
        float h = Mth.sin(f);
        float i = Mth.cos(f);
        float j = Mth.sin(g);
        float k = Mth.cos(g);
        boolean bl = j > 0.0f;
        boolean bl2 = h < 0.0f;
        boolean bl3 = k > 0.0f;
        float l = bl ? j : -j;
        float m = bl2 ? -h : h;
        float n = bl3 ? k : -k;
        float o = l * i;
        float p = n * i;
        Direction direction = bl ? Direction.EAST : Direction.WEST;
        Direction direction2 = bl2 ? Direction.UP : Direction.DOWN;
        Direction direction4 = direction3 = bl3 ? Direction.SOUTH : Direction.NORTH;
        if (l > n) {
            if (m > o) {
                return listClosest(direction2, direction, direction3);
            }
            if (p > m) {
                return listClosest(direction, direction3, direction2);
            }
            return listClosest(direction, direction2, direction3);
        }
        if (m > p) {
            return listClosest(direction2, direction3, direction);
        }
        if (o > m) {
            return listClosest(direction3, direction, direction2);
        }
        return listClosest(direction3, direction2, direction);
    }

    private static Direction[] listClosest(Direction first, Direction second, Direction third) {
        return new Direction[]{first, second, third, third.getOpposite(), second.getOpposite(), first.getOpposite()};
    }

    public static Direction[] getPlacementDirections(Vec3 clickVec, BlockPos placePos, Direction offsetDir) {
        int i;
        Direction[] directions = getEntityFacingOrder((float) getYaw(clickVec), (float) getPitch(clickVec));
        if (BlockUtil.canPlaceIn(placePos.relative(offsetDir))) {
            return directions;
        }
        Direction direction = offsetDir.getOpposite();
        for (i = 0; i < directions.length && directions[i] != direction.getOpposite(); ++i) {
        }
        if (i > 0) {
            System.arraycopy(directions, 0, directions, 1, i);
            directions[0] = direction.getOpposite();
        }
        return directions;
    }
}
