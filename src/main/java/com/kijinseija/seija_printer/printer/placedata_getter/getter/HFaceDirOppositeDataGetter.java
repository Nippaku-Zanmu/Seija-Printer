package com.kijinseija.seija_printer.printer.placedata_getter.getter;

import com.kijinseija.seija_printer.printer.util.PlaceData;
import com.kijinseija.seija_printer.printer.placedata_getter.AbstractDataGetter;
import com.kijinseija.seija_printer.printer.util.SeijaUtil;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class HFaceDirOppositeDataGetter extends AbstractDataGetter {
    //末影箱,熔炉,抽象红色门(?中继器,比较器)
    //ctx.getHorizontalPlayerFacing().getOpposite()
    //这种方块面为玩家水平朝向取反
    @Override
    public PlaceData getData(BlockPos pos, BlockState needState, List<Direction> dirs) {
        for (Direction dir : dirs) {
            Vec3d hitVec = pos.toCenterPos().offset(dir, 0.5);
            if (Direction.fromRotation(SeijaUtil.getYaw(hitVec)).getOpposite() == needState.get(HorizontalFacingBlock.FACING))
                return new PlaceData(pos.offset(dir), dir.getOpposite(), hitVec, true, null);
        }
        return new PlaceData(null, null, null, false, null);
    }


    @Override
    public boolean isSuitable(BlockState needState, BlockPos pos) {
        Block b = needState.getBlock();
        return b instanceof EnderChestBlock
            || b instanceof AbstractFurnaceBlock
            || b instanceof AbstractRedstoneGateBlock
            || b instanceof LecternBlock
            ||b instanceof StonecutterBlock
            ||b instanceof BeehiveBlock;
    }
}
