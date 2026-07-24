/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.util;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import com.kijinseija.seija_printer.utils.player.ChatUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SurfaceUtil {
    private static final Printer pri = Printer.getINSTANCE();

    public static boolean surfaceCheck(BlockPos pos, int c) {
        if (c == 0) return true;
        Set<BlockPos> surface;//= getSurfaceManhattan(pos, c);
        switch (pri.eSetSurfaceMode.get()) {
            case MANHATTAN:
                surface = getSurfaceManhattan(pos, c);
                break;
            case CHEBYSHEV:
                surface = getSurfaceChebyshev(pos, c);
                break;
            default:
                surface = new HashSet<>();
                surface.add(pos);
        }
        surface.remove(pos);
        for (BlockPos blockPos : surface) {

            if ((!BlockReplaceUtils.INSTANCE.getScheState(blockPos).isSolid())
                ||(!BlockReplaceUtils.INSTANCE.getScheState(blockPos).isCollisionShapeFullBlock(SchematicWorldHandler.getSchematicWorld(),blockPos))) return true;
        }
        return false;
    }

    public static Set<BlockPos> getSurfaceChebyshev(BlockPos pos, int c) {
        Set<BlockPos> result = new HashSet<>();

        for (int x = pos.getX() - c; x <= pos.getX() + c; x++) {
            for (int y = pos.getY() - c; y <= pos.getY() + c; y++) {
                for (int z = pos.getZ() - c; z <= pos.getZ() + c; z++) {
                    result.add(new BlockPos(x, y, z));
                }
            }
        }

        return result;
    }

    public static Set<BlockPos> getSurfaceManhattan(BlockPos pos, int c) {
        Set<BlockPos> result = new HashSet<>();
        result.add(pos);
        for (int i = 0; i < c; i++) {
            result = getSurfaceManhattan(result);
        }
        return result;
    }

    private static Set<BlockPos> getSurfaceManhattan(Set<BlockPos> set) {
        return set.stream()
            .flatMap(blockPos -> Arrays.stream(Direction.values())
                .map(blockPos::relative)
                .toList().stream())
            .collect(Collectors.toSet());
    }
}
