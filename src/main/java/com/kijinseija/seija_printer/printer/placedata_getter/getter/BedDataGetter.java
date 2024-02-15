package com.kijinseija.seija_printer.printer.placedata_getter.getter;

import com.kijinseija.seija_printer.printer.placedata_getter.AbstractDataGetter;
import com.kijinseija.seija_printer.printer.util.BlockUtil;
import com.kijinseija.seija_printer.printer.util.PlaceData;
import com.kijinseija.seija_printer.printer.util.SeijaUtil;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.BedPart;
import net.minecraft.client.MinecraftClient;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class BedDataGetter extends AbstractDataGetter {
    MinecraftClient mc = MinecraftClient.getInstance();

    @Override
    public PlaceData getData(BlockPos pos, BlockState needState, List<Direction> dirs) {
        Direction needDir = needState.get(Properties.HORIZONTAL_FACING);
        for (Direction clickDir : dirs) {
            Vec3d hitVec = pos.toCenterPos().offset(clickDir, 0.5);
            Direction faceDir = Direction.fromRotation(SeijaUtil.getYaw(hitVec));
            if (needDir == faceDir && BlockUtil.canPlaceIn(pos.offset(needDir))) {
                return new PlaceData(pos.offset(clickDir),clickDir.getOpposite(),hitVec,true,null);
            }
        }
        return new PlaceData(null,null,null,false,null);
    }

    @Override
    public boolean isSuitable(BlockState needState, BlockPos pos) {
        return needState.getBlock() instanceof BedBlock && needState.get(Properties.BED_PART) == BedPart.FOOT;
    }
}
