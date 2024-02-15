package com.kijinseija.seija_printer.printer.placedata_getter.getter;

import com.kijinseija.seija_printer.printer.placedata_getter.AbstractDataGetter;
import com.kijinseija.seija_printer.printer.util.PlaceData;
import net.minecraft.block.BlockState;
import net.minecraft.block.TorchBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class TorchBlockDataGetter extends AbstractDataGetter {
    @Override
    public PlaceData getData(BlockPos pos, BlockState needState, List<Direction> dirs) {
        boolean normal = false;
        Direction direction = Direction.WEST;
        try {
             direction = needState.get(Properties.HORIZONTAL_FACING);
        }catch (IllegalArgumentException e){
            normal = true;
        }


        for (Direction dir : dirs) {
            Vec3d clickVec = pos.toCenterPos().offset(dir,0.5);
            if (normal?dir!=Direction.DOWN:dir.getOpposite()!=direction)continue;
            return new PlaceData(pos.offset(dir),dir.getOpposite(),clickVec,true,null);
        }
        return new PlaceData(null,null,null,false,null);
    }

    @Override
    public boolean isSuitable(BlockState needState, BlockPos pos) {
        return needState.getBlock() instanceof TorchBlock;
    }
}
