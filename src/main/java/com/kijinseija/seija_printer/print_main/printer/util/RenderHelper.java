package com.kijinseija.seija_printer.print_main.printer.util;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.RainbowColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;


public class RenderHelper {
    public static final RainbowColor COLOR = new RainbowColor();

    public static PosInfo getBlackInfo(BlockPos p) {
        return new PosInfo(p, System.currentTimeMillis(), new Color(COLOR.r, COLOR.g, COLOR.b));
    }

    public static void drawBoxOutline(/*BlockPos pos,*/ Box box, Color col, Render3DEvent event) {

        VoxelShape shape = VoxelShapes.cuboid(box);
        shape.forEachEdge((minX, minY, minZ, maxX, maxY, maxZ) -> {
           // event.renderer.line(pos.getX() + minX, pos.getY() + minY, pos.getZ() + minZ, pos.getX() + maxX, pos.getY() + maxY, pos.getZ() + maxZ, col);
            event.renderer.line(minX, minY, minZ, maxX, maxY, maxZ, col);

        });
    }
}
