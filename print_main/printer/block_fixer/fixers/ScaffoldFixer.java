/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.block_fixer.fixers;

import com.kijinseija.seija_printer.print_main.printer.block_fixer.AbstractFixer;
import com.kijinseija.seija_printer.print_main.printer.util.BlockRotDataGetter;
import com.kijinseija.seija_printer.print_main.printer.util.BlockUtil;
import com.kijinseija.seija_printer.print_main.printer.util.InvUtil;
import com.kijinseija.seija_printer.print_main.printer.util.SeijaUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
import com.kijinseija.seija_printer.print_main.printer.util.records.DirDataI;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
import com.kijinseija.seija_printer.print_main.printer.util.records.PosInfo;
import com.kijinseija.seija_printer.settings.core.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import java.util.List;

public class ScaffoldFixer extends AbstractFixer {


    public ScaffoldFixer() {
        super("ScaffoldFix");
    }

    @Override
    public int fixBlock(BlockPos pos, BlockState needState) {
        for (Direction off : Direction.values()) {
            if (off == Direction.UP) continue;
            BlockPos helperPos = pos.relative(off);
            if (!(mc.level.getBlockState(helperPos).getBlock() instanceof ScaffoldingBlock
            &&mc.level.getBlockState(helperPos).getValue(ScaffoldingBlock.DISTANCE)<6)) continue;
            List<Direction> dirs = BlockUtil.getSortedDirs(helperPos,true);
            if (off != Direction.DOWN) {

                if (!dirs.contains(Direction.UP)) continue;
                DirData data = new DirData(helperPos, dirs);

                for (Vec3 clickVec : data.clickVecsInte(Direction.UP)) {

                    PlaceData interactData = PlaceData.NULL;
                    if (pri().bSetIllegalRotate.get()) {
                        interactData = PlaceData.newInstance(helperPos, Direction.UP, clickVec, true, BlockRotDataGetter.getRotateDataFromDir(off.getOpposite()));
                    } else if (Direction.fromYRot(SeijaUtil.getYaw(clickVec)) == off.getOpposite())
                        //1.21 Direction.fromRotation
                    {
                        interactData = PlaceData.newInstance(helperPos, Direction.UP, clickVec, true, null);
                    }
                    if (interactData.valid()){
                        if (InvUtil.switchBlock(Blocks.SCAFFOLDING)) {
                            BlockUtil.interactBlock(interactData);
                            PosInfo blackInfo = new PosInfo(pos, off, clickVec, false, System.currentTimeMillis(), Color.BLACK);
                            pri().blackList.add(blackInfo);
                            return SUCCESS;
                        }else return RETURN;
                    }
                }
            } else {
                dirs.remove(Direction.UP);
                DirData data = new DirData(helperPos, dirs);
                for (Direction clickDir : dirs) {
                    for (Vec3 clickVec : data.clickVecsInte(clickDir)) {
                        PlaceData interactData =  PlaceData.newInstance(helperPos, clickDir, clickVec, true, null);
                        if (InvUtil.switchBlock(Blocks.SCAFFOLDING)) {
                            BlockUtil.interactBlock(interactData);
                            PosInfo blackInfo = new PosInfo(pos, off, clickVec, false, System.currentTimeMillis(), Color.BLACK);
                            pri().blackList.add(blackInfo);
                            return SUCCESS;
                        }else return RETURN;
                    }
                }
            }
        }
        return CONTINUE;
    }

    @Override
    public boolean needFix(BlockPos pos, BlockState needState) {
        return mc.level.getBlockState(pos).getBlock()instanceof AirBlock
            &&needState.getBlock()instanceof ScaffoldingBlock;
    }
}
