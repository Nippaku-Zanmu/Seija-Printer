package com.kijinseija.seija_printer.print_main.printer.util.records;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;

public record PlaceData(BlockPos pos, Direction dir, Vec3d hitVec, boolean valid, @Nullable RotationData exRotateData,
                        @Nullable BooleanSupplier test) {
    public static final PlaceData NULL = new PlaceData(null, null, null, false, null,null);

    public static PlaceData newInstance(BlockPos pos, Direction dir, Vec3d hitVec, boolean valid, @Nullable RotationData exRotateData) {
        return new PlaceData(pos, dir, hitVec, valid, exRotateData,null);
    }
}
