package com.kijinseija.seija_printer.printer.placedata_getter.getter;

import com.kijinseija.seija_printer.printer.placedata_getter.AbstractDataGetter;
import com.kijinseija.seija_printer.printer.util.BlockUtil;
import com.kijinseija.seija_printer.printer.util.PlaceData;
import com.kijinseija.seija_printer.printer.util.SeijaUtil;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class ObserverDataGetter extends AbstractDataGetter {
    @Override
    public PlaceData getData(BlockPos pos, BlockState needState, List<Direction> dirs) {
        for (Direction dir : dirs) {
            Vec3d hitVec = pos.toCenterPos().offset(dir, 0.5);
            Direction lookFac = BlockUtil.getEntityFacingOrder((float) SeijaUtil.getYaw(hitVec), (float) SeijaUtil.getPitch(hitVec))[0];
            if (lookFac.toString().toLowerCase().equals(needState.get(FacingBlock.FACING).toString()))
                return new PlaceData(pos.offset(dir), dir.getOpposite(), hitVec, true, null);
        }
        return new PlaceData(null, null, null, false, null);
    }


    @Override
    public boolean isSuitable(BlockState needState, BlockPos pos) {
        return needState.getBlock() instanceof ObserverBlock;

    }
}
