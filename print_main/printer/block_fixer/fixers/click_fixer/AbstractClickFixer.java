/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.block_fixer.fixers.click_fixer;

import com.kijinseija.seija_printer.print_main.printer.block_fixer.AbstractFixer;
import com.kijinseija.seija_printer.print_main.printer.util.BlockUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
import com.kijinseija.seija_printer.print_main.printer.util.records.DirDataI;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
import com.kijinseija.seija_printer.settings.core.BoolSetting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import java.util.List;

public abstract class AbstractClickFixer extends AbstractFixer {
    public AbstractClickFixer(String name) {
        super(name);
    }
    public AbstractClickFixer(BoolSetting setting){
        super(setting);

    }

    @Override
    public int fixBlock(BlockPos pos, BlockState needState) {

//        BlockUtil.interactBlock(pos,interactDir.get(0));
        DirData dirData = new DirData(pos, BlockUtil.getSortedDirs(pos,true));
        for (Direction dir : dirData.dirs()) {
            for (Vec3 clickVec : dirData.clickVecsInte(dir)) {
                BlockUtil.interactBlock(PlaceData.newInstance(dirData.placePos(),dir,clickVec,true,null));
                return AbstractFixer.SUCCESS;
            }
        }
        return AbstractFixer.CONTINUE;
    }

    @Override
    public boolean needFix(BlockPos pos, BlockState needState) {
        List<Direction> interactDir = BlockUtil.getSortedDirs(pos,true);
        if (interactDir.isEmpty())
            return false;
        return true;
    }
}
