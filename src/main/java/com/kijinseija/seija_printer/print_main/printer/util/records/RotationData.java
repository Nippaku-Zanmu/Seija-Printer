package com.kijinseija.seija_printer.print_main.printer.util.records;

import com.kijinseija.seija_printer.print_main.printer.util.SeijaUtil;
import net.minecraft.util.math.Vec3d;

public record RotationData(double yaw, double pitch) {
    public static RotationData fromVec(Vec3d vec){
        return new RotationData(SeijaUtil.getYaw(vec),SeijaUtil.getPitch(vec));
    }
}
