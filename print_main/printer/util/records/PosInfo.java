/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.util.records;

import com.kijinseija.seija_printer.settings.core.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public record PosInfo(BlockPos pos, Direction torchDir, Vec3 clickVec,boolean isPlaceMode, long timestamp, Color renderColor) {
    }
