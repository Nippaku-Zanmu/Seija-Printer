/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.util.records;

import org.jetbrains.annotations.Nullable;


import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public record PlaceData(BlockPos pos, Direction dir, Vec3 hitVec, boolean valid, @Nullable RotationData exRotateData,
                        @Nullable BooleanSupplier test) {
    public static final PlaceData NULL = new PlaceData(null, null, null, false, null,null);

    public static PlaceData newInstance(BlockPos pos, Direction dir, Vec3 hitVec, boolean valid, @Nullable RotationData exRotateData) {
        return new PlaceData(pos, dir, hitVec, valid, exRotateData,null);
    }
}
