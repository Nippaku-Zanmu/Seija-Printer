package com.kijinseija.seija_printer.print_main.printer.util;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class PosSorter {
    static MinecraftClient mc = MinecraftClient.getInstance();
    static Printer pri = Printer.getINSTANCE();

    public static List<BlockPos> sort(List<BlockPos> l) {
        l.sort((pos1, pos2) -> {
            if (BlockReplaceUtils.INSTANCE.getScheState(pos1).getBlock().asItem()
                .equals(mc.player.getMainHandStack().getItem()) ^
                BlockReplaceUtils.INSTANCE.getScheState(pos2).getBlock().asItem()
                    .equals(mc.player.getMainHandStack().getItem())) {
                return BlockReplaceUtils.INSTANCE.getScheState(pos1).getBlock().asItem()
                    .equals(mc.player.getMainHandStack().getItem()) ? -1 : 1;
            }
            int angleVal ;
            switch (pri.angleSortMode.get()){
                case LOW -> angleVal = sortAngle(pos1, pos2);
                case HIGH -> angleVal = -sortAngle(pos1, pos2);
                default -> angleVal = 0;
            }

            if (angleVal != 0) return angleVal;
            else {
                int distanceVal;
                switch (pri.distanceSortMode.get()) {
                    case HIGH -> distanceVal = -Integer.compare((int) pos1.toCenterPos().distanceTo(mc.player.getEyePos()), Integer.valueOf((int) pos2.toCenterPos().distanceTo(mc.player.getEyePos())));
                    case LOW -> distanceVal = Integer.compare((int) pos1.toCenterPos().distanceTo(mc.player.getEyePos()), Integer.valueOf((int) pos2.toCenterPos().distanceTo(mc.player.getEyePos())));
                    default ->  distanceVal = 0;
                }
                return distanceVal;
            }
        });
        return l;
    }

    private static int sortAngle(BlockPos pos1, BlockPos pos2) {
        double e1yaw = Math.abs(SeijaUtil.getYaw(pos1.toCenterPos()) - mc.player.getYaw());
        double e2yaw = Math.abs(SeijaUtil.getYaw(pos2.toCenterPos()) - mc.player.getYaw());

        double e1pitch = Math.floor(Math.abs(SeijaUtil.getPitch(pos1.toCenterPos()) - mc.player.getPitch())/20);
        double e2pitch = Math.floor(Math.abs(SeijaUtil.getPitch(pos2.toCenterPos()) - mc.player.getPitch())/20);

        return Double.compare(e1yaw * e1yaw + e1pitch * e1pitch, e2yaw * e2yaw + e2pitch * e2pitch);
    }
}
