/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.util;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public class PosSorter {
    static Minecraft mc = Minecraft.getInstance();
    static Printer pri = Printer.getINSTANCE();

    public static List<BlockPos> sort(List<BlockPos> l) {
        l.sort((pos1, pos2) -> {
            if (BlockReplaceUtils.INSTANCE.getScheState(pos1).getBlock().asItem()
                .equals(mc.player.getMainHandItem().getItem()) ^
                BlockReplaceUtils.INSTANCE.getScheState(pos2).getBlock().asItem()
                    .equals(mc.player.getMainHandItem().getItem())) {
                return BlockReplaceUtils.INSTANCE.getScheState(pos1).getBlock().asItem()
                    .equals(mc.player.getMainHandItem().getItem()) ? -1 : 1;
            }
            int angleVal ;
            switch (pri.eSetAngleSortMode.get()){
                case LOW -> angleVal = sortAngle(pos1, pos2);
                case HIGH -> angleVal = -sortAngle(pos1, pos2);
                default -> angleVal = 0;
            }

            if (angleVal != 0) return angleVal;
            else {
                int distanceVal;
                switch (pri.eSetDistanceSortMode.get()) {
                    case HIGH -> distanceVal = -Integer.compare((int) pos1.getCenter().distanceToSqr(mc.player.getEyePosition()), Integer.valueOf((int) pos2.getCenter().distanceToSqr(mc.player.getEyePosition())));
                    case LOW -> distanceVal = Integer.compare((int) pos1.getCenter().distanceToSqr(mc.player.getEyePosition()), Integer.valueOf((int) pos2.getCenter().distanceToSqr(mc.player.getEyePosition())));
                    default ->  distanceVal = 0;
                }
                return distanceVal;
            }
        });
        return l;
    }

    private static int sortAngle(BlockPos pos1, BlockPos pos2) {
        double e1yaw = Math.abs(SeijaUtil.getYaw(pos1.getCenter()) - mc.player.getYRot());
        double e2yaw = Math.abs(SeijaUtil.getYaw(pos2.getCenter()) - mc.player.getYRot());

        double e1pitch = Math.floor(Math.abs(SeijaUtil.getPitch(pos1.getCenter()) - mc.player.getXRot())/20);
        double e2pitch = Math.floor(Math.abs(SeijaUtil.getPitch(pos2.getCenter()) - mc.player.getXRot())/20);

        return Double.compare(e1yaw * e1yaw + e1pitch * e1pitch, e2yaw * e2yaw + e2pitch * e2pitch);
    }
}
