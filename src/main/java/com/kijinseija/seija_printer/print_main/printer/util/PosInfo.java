package com.kijinseija.seija_printer.print_main.printer.util;

import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.util.math.BlockPos;

public record PosInfo(BlockPos pos, long timestamp, Color renderColor) {
    }
