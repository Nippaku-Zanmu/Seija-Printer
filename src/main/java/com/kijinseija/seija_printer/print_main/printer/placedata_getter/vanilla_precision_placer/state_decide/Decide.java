package com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer.state_decide;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface Decide {
    boolean isSuit(BlockState needState, BlockState nowState, BlockPos placePos);
    boolean test(BlockState needState,BlockState nowState,BlockPos placePos);
}
