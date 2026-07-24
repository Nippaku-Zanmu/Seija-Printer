/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.util.records;

import com.kijinseija.seija_printer.print_main.printer.util.SeijaUtil;
import java.util.Objects;
import net.minecraft.world.phys.Vec3;

public record RotationData(double yaw, double pitch,Runnable task) {
    public static RotationData build(double yaw, double pitch){
        return new RotationData(yaw,pitch, null);
    }
    public static RotationData fromVec(Vec3 vec){
        return new RotationData(SeijaUtil.getYaw(vec),SeijaUtil.getPitch(vec),null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RotationData that = (RotationData) o;
        return Double.compare(yaw, that.yaw) == 0 && Double.compare(pitch, that.pitch) == 0 && Objects.equals(task, that.task);
    }

    @Override
    public int hashCode() {
        int result = Double.hashCode(yaw);
        result = 31 * result + Double.hashCode(pitch);
        result = 31 * result + Objects.hashCode(task);
        return result;
    }
}
