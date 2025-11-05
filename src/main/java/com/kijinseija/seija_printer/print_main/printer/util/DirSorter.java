/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class DirSorter {
    public static List<Direction> sort(List<Direction> dirs, BlockPos placePos) {
        dirs.sort(Comparator.comparingDouble(d -> getSize(d, placePos)));
        return dirs;
    }

    private static double getSize(Direction dir, BlockPos pos) {
        Vec3d cent = pos.toCenterPos().offset(dir, 0.5);
        LinkedList<Direction.Axis> axes = new LinkedList<>(List.of(Direction.Axis.values()));
        axes.remove(dir.getAxis());
        Vec3d corner = cent.add(Vec3d.ZERO.withAxis(axes.get(0), -0.5)).add(Vec3d.ZERO.withAxis(axes.get(1), -0.5));
        Vec3d corner1 = cent.add(Vec3d.ZERO.withAxis(axes.get(0), 0.5)).add(Vec3d.ZERO.withAxis(axes.get(1), 0.5));
        return calculateAngelDistance(corner,corner1);
    }


    public static double calculateAngelDistance(Vec3d v1,Vec3d v2) {
        // 将角度转换为弧度

        double pitch1Rad = Math.toRadians(SeijaUtil.getPitch(v1));
        double yaw1Rad = Math.toRadians(SeijaUtil.getYaw(v1));
        double pitch2Rad = Math.toRadians(SeijaUtil.getPitch(v2));
        double yaw2Rad = Math.toRadians(SeijaUtil.getYaw(v2));

        // 使用球面距离公式计算距离
        double centralAngle = 2 * Math.asin(Math.sqrt(
            Math.pow(Math.sin((pitch2Rad - pitch1Rad) / 2), 2) +
                Math.cos(pitch1Rad) * Math.cos(pitch2Rad) * Math.pow(Math.sin((yaw2Rad - yaw1Rad) / 2), 2)
        ));

        // 计算球面上的距离
        return centralAngle;
    }
}
