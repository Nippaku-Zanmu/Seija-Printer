package com.kijinseija.seija_printer.printer.block_fixer.fixers;

import com.kijinseija.seija_printer.printer.block_fixer.AbstractFixer;
import com.kijinseija.seija_printer.printer.util.BlockUtil;
import com.kijinseija.seija_printer.printer.util.InvUtil;
import com.kijinseija.seija_printer.printer.util.PlaceData;
import com.kijinseija.seija_printer.printer.Printer;
import com.kijinseija.seija_printer.printer.util.RenderHelper;
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
import java.util.List;

public class SlabFixer extends AbstractFixer {
    @Override
    public boolean fixBlock(BlockPos pos, BlockState needState) {
        PlaceData data = null;
        BlockState blockState = mc.world.getBlockState(pos);
        SlabType slabType = blockState.get(Properties.SLAB_TYPE);
        switch (slabType){
            case TOP -> slabType = SlabType.BOTTOM;
            case BOTTOM -> slabType=SlabType.TOP;
        }
        List<Direction> dirs = new ArrayList<>(BlockUtil.getDirs(pos));
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
                    (helperState.get(SlabBlock.TYPE) != SlabType.DOUBLE&&
                        helperState.get(SlabBlock.TYPE) == slabType
                        && dir.getAxis() == Direction.Axis.Y))
            ) continue;
            if (dir.getAxis() == Direction.Axis.Y) {
                data = new PlaceData(pos.offset(dir), dir.getOpposite()
                    , pos.toCenterPos().offset(dir, 0.5), true, null);
                break;
            }
            data =
                new PlaceData(pos.offset(dir), dir.getOpposite()
                    , pos.toCenterPos().offset(dir, 0.5)
                    .add(0, slabType == SlabType.BOTTOM ? -0.1 : 0.1, 0), true, null);
            break;
        }
        if (data == null) {
            final Vec3d centerPos = pos.toCenterPos();
            switch (slabType) {
                case BOTTOM -> {
                    if ((!pri.strictDir.get()) || centerPos.getY() >= mc.player.getEyePos().getY() - 0.4)
                        data = new PlaceData(pos, Direction.DOWN, centerPos, true, null);
                }
                case TOP -> {
                    if ((!pri.strictDir.get()) || centerPos.getY() <= mc.player.getEyePos().getY() + 0.4)
                        data = new PlaceData(pos, Direction.UP, centerPos, true, null);
                }
            }
        }
        if (data != null) {
            if (!InvUtil.switchBlock(needState.getBlock())) {
                return false;
            }

            BlockUtil.placeBlock(data);


            return true;
        }
        return false;
    }

    @Override
    public boolean needFix(BlockPos pos, BlockState needState) {

        BlockState blockState = mc.world.getBlockState(pos);
        if (blockState.getBlock() instanceof AirBlock||needState.getBlock()instanceof AirBlock)
            return false;
        if ((!(needState.getBlock() instanceof SlabBlock)) || (!blockState.getBlock().equals (needState.getBlock()))) {
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
