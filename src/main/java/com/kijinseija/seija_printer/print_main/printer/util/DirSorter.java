/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.util;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class DirSorter {
    private static final Minecraft mc = Minecraft.getInstance();

    //AI生成 将Dirs基于方块坐标按可见的面积降序
    public static List<Direction> sort(List<Direction> dirs, BlockPos placePos,boolean interact) {
        Vec3 blockCenter = placePos.getCenter();

        Vec3 eyePos = mc.player.getEyePosition();
        // 视线向量（不归一化）
        double vx = eyePos.x - blockCenter.x;
        double vy = eyePos.y - blockCenter.y;
        double vz = eyePos.z - blockCenter.z;

        dirs.sort(Comparator.comparingDouble(dir -> {
            var n = dir.getUnitVec3i();
            double score = n.getX() * vx + n.getY() * vy + n.getZ() * vz;
            return -score*(interact?1:-1);
        }));
        return dirs;
    }

  //  private static double getSize(Direction dir, BlockPos pos) {
//        return mc.player.getEyePos().squaredDistanceTo()
//        Vec3d cent = pos.toCenterPos().offset(dir, 0.5);
//        LinkedList<Direction.Axis> axes = new LinkedList<>(List.of(Direction.Axis.values()));
//        axes.remove(dir.getAxis());
//        Vec3d corner = cent.add(Vec3d.ZERO.withAxis(axes.get(0), -0.5)).add(Vec3d.ZERO.withAxis(axes.get(1), -0.5));
//        Vec3d corner1 = cent.add(Vec3d.ZERO.withAxis(axes.get(0), 0.5)).add(Vec3d.ZERO.withAxis(axes.get(1), 0.5));
//        return calculateAngelDistance(corner,corner1);
//    }


//    public static double calculateAngelDistance(Vec3d v1,Vec3d v2) {
//        // 将角度转换为弧度
//
//        double pitch1Rad = Math.toRadians(SeijaUtil.getPitch(v1));
//        double yaw1Rad = Math.toRadians(SeijaUtil.getYaw(v1));
//        double pitch2Rad = Math.toRadians(SeijaUtil.getPitch(v2));
//        double yaw2Rad = Math.toRadians(SeijaUtil.getYaw(v2));
//
//        // 使用球面距离公式计算距离
//        double centralAngle = 2 * Math.asin(Math.sqrt(
//            Math.pow(Math.sin((pitch2Rad - pitch1Rad) / 2), 2) +
//                Math.cos(pitch1Rad) * Math.cos(pitch2Rad) * Math.pow(Math.sin((yaw2Rad - yaw1Rad) / 2), 2)
//        ));
//
//        // 计算球面上的距离
//        return centralAngle;
//    }
}
