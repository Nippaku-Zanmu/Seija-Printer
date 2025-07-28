package com.kijinseija.seija_printer.print_main.printer.util.records;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;

public record DirDataI(BlockPos placePos, List<Direction> dirs) {
    private static Printer pri = Printer.getINSTANCE();


}
