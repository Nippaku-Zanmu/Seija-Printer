/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.util;

import com.kijinseija.seija_printer.print_main.printer.util.records.PosInfo;
import com.kijinseija.seija_printer.events.render.Render3DEvent;
import com.kijinseija.seija_printer.settings.core.Color;
import com.kijinseija.seija_printer.settings.core.RainbowColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;


public class RenderHelper {
    public static final RainbowColor COLOR = new RainbowColor();

    public static PosInfo getBlackInfo(BlockPos p, Direction clickDir, Vec3 clickVec,boolean isPlace) {
        return new PosInfo(p,clickDir,clickVec,isPlace, System.currentTimeMillis(), new Color(COLOR.r, COLOR.g, COLOR.b));
    }

    public static void drawBoxOutline(/*BlockPos pos,*/ AABB box, Color col, Render3DEvent event) {

        VoxelShape shape = Shapes.create(box);
        shape.forAllEdges((minX, minY, minZ, maxX, maxY, maxZ) -> {
           // event.renderer.line(pos.getX() + minX, pos.getY() + minY, pos.getZ() + minZ, pos.getX() + maxX, pos.getY() + maxY, pos.getZ() + maxZ, col);
            event.renderer.line(minX, minY, minZ, maxX, maxY, maxZ, col);

        });
    }
}
