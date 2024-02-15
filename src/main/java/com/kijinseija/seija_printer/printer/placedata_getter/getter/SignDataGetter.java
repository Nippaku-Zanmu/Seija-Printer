package com.kijinseija.seija_printer.printer.placedata_getter.getter;

import com.kijinseija.seija_printer.printer.placedata_getter.AbstractDataGetter;
import com.kijinseija.seija_printer.printer.util.PlaceData;
import com.kijinseija.seija_printer.printer.util.SeijaUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class SignDataGetter extends AbstractDataGetter {
    @Override
    public PlaceData getData(BlockPos pos, BlockState needState, List<Direction> dirs) {
        if (needState.getBlock() instanceof WallSignBlock) {
            Direction needDir = needState.get(Properties.HORIZONTAL_FACING);
            for (Direction dir : dirs) {
                if (dir.getAxis() == Direction.Axis.Y||dir.getOpposite()!=needDir) continue;
                Vec3d clickVec = pos.toCenterPos().offset(dir, 0.5);
                return new PlaceData(pos.offset(dir),dir.getOpposite(),clickVec,true,null);
            }

        } else if (needState.getBlock() instanceof SignBlock&&dirs.contains(Direction.DOWN)) {
            Integer rotation = needState.get(Properties.ROTATION);
            Vec3d hitVec= pos.toCenterPos().offset(Direction.DOWN,0.5);
            if (RotationPropertyHelper.fromYaw((float) (SeijaUtil.getYaw(hitVec) + 180.0f))==rotation) {
                return new PlaceData(pos.offset(Direction.DOWN),Direction.UP,hitVec,true,null);
            }
        }
        return new PlaceData(null,null,null,false,null);
    }

    @Override
    public boolean isSuitable(BlockState needState, BlockPos pos) {

        Block needBlock = needState.getBlock();
        return needBlock instanceof SignBlock
            || needBlock instanceof WallSignBlock;
    }
}
