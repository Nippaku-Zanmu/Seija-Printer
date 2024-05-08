package com.kijinseija.seija_printer.print_main.printer.block_fixer.fixers;

import com.kijinseija.seija_printer.print_main.printer.block_fixer.AbstractFixer;
import com.kijinseija.seija_printer.print_main.printer.util.BlockUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
import com.kijinseija.seija_printer.print_main.printer.util.InvUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class SlabFixer extends AbstractFixer {
    @Override
    public int fixBlock(BlockPos pos, BlockState needState) {
        PlaceData data = null;
        BlockState blockState = mc.world.getBlockState(pos);
        SlabType slabType = blockState.get(Properties.SLAB_TYPE);
        switch (slabType) {
            case TOP -> slabType = SlabType.BOTTOM;
            case BOTTOM -> slabType = SlabType.TOP;
        }
        List<Direction> dirs = new ArrayList<>(BlockUtil.getDirs(pos));
        DirData dirData = new DirData(pos, dirs);
        switch (slabType) {
            case TOP -> dirs.remove(Direction.DOWN);
            case BOTTOM -> dirs.remove(Direction.UP);
        }
        for (Direction dir : dirs) {
            BlockState helperState = mc.world.getBlockState(pos.offset(dir));
            if (helperState.getBlock().equals(needState.getBlock())
                && (
                (helperState.get(SlabBlock.TYPE) != SlabType.DOUBLE
                    && helperState.get(SlabBlock.TYPE) != slabType
                    && dir.getAxis() != Direction.Axis.Y)
                    ||
                    (helperState.get(SlabBlock.TYPE) != SlabType.DOUBLE &&
                        helperState.get(SlabBlock.TYPE) == slabType
                        && dir.getAxis() == Direction.Axis.Y))
            ) continue;
            if (dir.getAxis() == Direction.Axis.Y) {
                for (Vec3d clickVec : dirData.clickVecs(dir, 0)) {
                    data = new PlaceData(pos.offset(dir), dir.getOpposite()
                        , clickVec, true, null);
                    break;
                }
                break;
            }

            for (Vec3d clickVec : dirData.clickVecs(dir, slabType == SlabType.BOTTOM ? 2 : 1)) {
                data = new PlaceData(pos.offset(dir), dir.getOpposite()
                    , clickVec, true, null);
                break;
            }
            break;
        }
        if (data == null) {
            final Vec3d centerPos = pos.toCenterPos();
            switch (slabType) {
                case BOTTOM -> {
                    if ((!pri.bSetStrictDir.get()) || centerPos.getY() >= mc.player.getEyePos().getY() - 0.4) {
                        for (Vec3d clickVec : dirData.clickVecs(Direction.UP)) {
                            data = new PlaceData(pos, Direction.DOWN, clickVec, true, null);

                        }
                    }
                }
                case TOP -> {
                    if ((!pri.bSetStrictDir.get()) || centerPos.getY() <= mc.player.getEyePos().getY() + 0.4)
                        for (Vec3d clickVec : dirData.clickVecs(Direction.DOWN)) {
                            data = new PlaceData(pos, Direction.UP, clickVec, true, null);

                        }

                }
            }
        }
        if (data != null) {
            if (!InvUtil.switchBlock(needState.getBlock())) {
                return AbstractFixer.RETURN;
            }

            BlockUtil.placeBlock(data);


            return AbstractFixer.SUCCESS;
        }
        return AbstractFixer.CONTINUE;
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
