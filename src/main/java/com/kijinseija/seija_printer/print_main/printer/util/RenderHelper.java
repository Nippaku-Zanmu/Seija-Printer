package com.kijinseija.seija_printer.print_main.printer.util;

import com.kijinseija.seija_printer.print_main.printer.util.records.PosInfo;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.RainbowColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;


public class RenderHelper {
    public static final RainbowColor COLOR = new RainbowColor();

    public static PosInfo getBlackInfo(BlockPos p, Direction clickDir, Vec3d clickVec,boolean isPlace) {
        return new PosInfo(p,clickDir,clickVec,isPlace, System.currentTimeMillis(), new Color(COLOR.r, COLOR.g, COLOR.b));
    }

    public static void drawBoxOutline(/*BlockPos pos,*/ Box box, Color col, Render3DEvent event) {

        VoxelShape shape = VoxelShapes.cuboid(box);
        shape.forEachEdge((minX, minY, minZ, maxX, maxY, maxZ) -> {
           // event.renderer.line(pos.getX() + minX, pos.getY() + minY, pos.getZ() + minZ, pos.getX() + maxX, pos.getY() + maxY, pos.getZ() + maxZ, col);
            event.renderer.line(minX, minY, minZ, maxX, maxY, maxZ, col);

        });
    }
}
