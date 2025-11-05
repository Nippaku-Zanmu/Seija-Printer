/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer.state_decide;

import meteordevelopment.meteorclient.settings.Setting;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public interface Decide {
    boolean isSuit(BlockState needState, BlockState nowState, BlockPos placePos);
    boolean test(BlockState needState,BlockState nowState,BlockPos placePos);
    @NotNull
    Setting<?>[] getSettings();
}
