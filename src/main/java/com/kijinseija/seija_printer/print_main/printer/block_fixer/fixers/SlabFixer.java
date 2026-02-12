/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.block_fixer.fixers;

import com.kijinseija.seija_printer.print_main.printer.block_fixer.AbstractFixer;
import com.kijinseija.seija_printer.print_main.printer.util.BlockUtil;
import com.kijinseija.seija_printer.print_main.printer.util.InvUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
import com.kijinseija.seija_printer.print_main.printer.util.records.DirDataI;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceDataPack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.Vec3;

public class SlabFixer extends AbstractFixer {
    public SlabFixer() {
        super("SlabFix");
    }

    @Override
    public int fixBlock(BlockPos pos, BlockState needState) {

        PlaceDataPack dataPack = null;
        BlockState blockState = mc.level.getBlockState(pos);
        SlabType slabType = blockState.getValue(BlockStateProperties.SLAB_TYPE);

        List<Direction> dirs = new ArrayList<>(BlockUtil.getSortedDirs(pos,false));
        DirData dirData = new DirData(pos, dirs);

        switch (slabType) {
            case TOP -> dirs.add(Direction.DOWN);
            case BOTTOM -> dirs.add(Direction.UP);
            //适应com/kijinseija/seija_printer/print_main/printer/util/BlockUtil.java中getDirs的方块阻挡检测
        }
        for (Direction dir : dirs) {
            BlockState helperState = mc.level.getBlockState(pos.relative(dir));
            if (helperState.getBlock().equals(needState.getBlock())
                && (
                (helperState.getValue(SlabBlock.TYPE) != SlabType.DOUBLE
                    && helperState.getValue(SlabBlock.TYPE) == slabType
                    && dir.getAxis() != Direction.Axis.Y)
                    ||
                    (helperState.getValue(SlabBlock.TYPE) != SlabType.DOUBLE &&
                        helperState.getValue(SlabBlock.TYPE) != slabType
                        && dir.getAxis() == Direction.Axis.Y))
            ) continue;
            if (dir.getAxis() == Direction.Axis.Y) {
                DirData data = new DirData(pos, Collections.singletonList(dir));
                for (Vec3 clickVec : data.clickVecsInte(dir, 0)) {
                    dataPack = PlaceDataPack.inte( PlaceData.newInstance(pos, dir
                        , clickVec, true, null));
                    break;
                }
                continue;
            }

            for (Vec3 clickVec : dirData.clickVecs(dir, slabType == SlabType.BOTTOM ? 1 : 2)) {
                dataPack = PlaceDataPack.plac( PlaceData.newInstance(pos.relative(dir), dir.getOpposite()
                    , clickVec, true, null));
                break;
            }
            break;
        }
        if (dataPack == null) {
            return CONTINUE;
        }

        if (!InvUtil.switchBlock(needState.getBlock())) {
            return AbstractFixer.RETURN;
        }

        if (dataPack.placeMode()) {
            BlockUtil.placeBlock(dataPack.data());//放置
        } else {
            BlockUtil.interactBlock(dataPack.data());
        }


        return AbstractFixer.SUCCESS;

    }

    @Override
    public boolean needFix(BlockPos pos, BlockState needState) {
        if (!InvUtil.findBlock(needState.getBlock())) return false;
        BlockState blockState = mc.level.getBlockState(pos);
        if (blockState.getBlock() instanceof AirBlock || needState.getBlock() instanceof AirBlock)
            return false;
        if ((!(needState.getBlock() instanceof SlabBlock)) || (!blockState.getBlock().equals(needState.getBlock()))) {
            return false;
        }

        //不同方块,不为台阶则不需要修复
        if (blockState.getValue(BlockStateProperties.SLAB_TYPE) != SlabType.DOUBLE
            && needState.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.DOUBLE) {
            return true;
        }

        return false;
    }
}
