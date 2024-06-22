package com.kijinseija.seija_printer.print_main.printer.block_fixer.fixers;

import com.kijinseija.seija_printer.print_main.printer.block_fixer.AbstractFixer;
import com.kijinseija.seija_printer.print_main.printer.util.BlockUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
import com.kijinseija.seija_printer.print_main.printer.util.InvUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.DirDataI;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceDataPack;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SlabFixer extends AbstractFixer {
    @Override
    public int fixBlock(BlockPos pos, BlockState needState) {

        PlaceDataPack dataPack = null;
        BlockState blockState = mc.world.getBlockState(pos);
        SlabType slabType = blockState.get(Properties.SLAB_TYPE);

        List<Direction> dirs = new ArrayList<>(BlockUtil.getSortedDirs(pos));
        DirData dirData = new DirData(pos, dirs);

        switch (slabType) {
            case TOP -> dirs.add(Direction.DOWN);
            case BOTTOM -> dirs.add(Direction.UP);
            //适应com/kijinseija/seija_printer/print_main/printer/util/BlockUtil.java中getDirs的方块阻挡检测
        }
        for (Direction dir : dirs) {
            BlockState helperState = mc.world.getBlockState(pos.offset(dir));
            if (helperState.getBlock().equals(needState.getBlock())
                && (
                (helperState.get(SlabBlock.TYPE) != SlabType.DOUBLE
                    && helperState.get(SlabBlock.TYPE) == slabType
                    && dir.getAxis() != Direction.Axis.Y)
                    ||
                    (helperState.get(SlabBlock.TYPE) != SlabType.DOUBLE &&
                        helperState.get(SlabBlock.TYPE) != slabType
                        && dir.getAxis() == Direction.Axis.Y))
            ) continue;
            if (dir.getAxis() == Direction.Axis.Y) {
                DirDataI dataI = new DirDataI(pos, Collections.singletonList(dir));
                for (Vec3d clickVec : dataI.clickVecs(dir, 0)) {
                    dataPack = PlaceDataPack.inte(new PlaceData(pos, dir
                        , clickVec, true, null));
                    break;
                }
                continue;
            }

            for (Vec3d clickVec : dirData.clickVecs(dir, slabType == SlabType.BOTTOM ? 1 : 2)) {
                dataPack = PlaceDataPack.plac(new PlaceData(pos.offset(dir), dir.getOpposite()
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
        BlockState blockState = mc.world.getBlockState(pos);
        if (blockState.getBlock() instanceof AirBlock || needState.getBlock() instanceof AirBlock)
            return false;
        if ((!(needState.getBlock() instanceof SlabBlock)) || (!blockState.getBlock().equals(needState.getBlock()))) {
            return false;
        }

        //不同方块,不为台阶则不需要修复
        if (blockState.get(Properties.SLAB_TYPE) != SlabType.DOUBLE
            && needState.get(Properties.SLAB_TYPE) == SlabType.DOUBLE) {
            return true;
        }

        return false;
    }
}
