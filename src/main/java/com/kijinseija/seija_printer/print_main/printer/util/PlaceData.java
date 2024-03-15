package com.kijinseija.seija_printer.print_main.printer.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public record PlaceData(BlockPos pos, Direction dir, Vec3d hitVec, boolean valid,@Nullable Vec3d exRotateVec) {
}
