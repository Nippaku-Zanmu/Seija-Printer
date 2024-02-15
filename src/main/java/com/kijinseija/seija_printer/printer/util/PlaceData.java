package com.kijinseija.seija_printer.printer.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public record PlaceData(BlockPos pos, Direction dir, Vec3d hitVec, boolean valid,Vec3d exRotateVec) {
}
