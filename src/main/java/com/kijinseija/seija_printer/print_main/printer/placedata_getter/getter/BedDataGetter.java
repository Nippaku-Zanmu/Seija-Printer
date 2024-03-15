package com.kijinseija.seija_printer.print_main.printer.placedata_getter.getter;

import com.kijinseija.seija_printer.print_main.printer.placedata_getter.AbstractDataGetter;
import com.kijinseija.seija_printer.print_main.printer.util.BlockUtil;
import com.kijinseija.seija_printer.print_main.printer.util.DirData;
import com.kijinseija.seija_printer.print_main.printer.util.PlaceData;
import com.kijinseija.seija_printer.print_main.printer.util.SeijaUtil;
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
    public PlaceData getData(BlockState needState, DirData dirData) {
        BlockPos pos = dirData.placePos();
        Direction needDir = needState.get(Properties.HORIZONTAL_FACING);
        for (Direction clickDir : dirData.dirs()) {
            for (Vec3d hitVec : dirData.clickVecs(clickDir)) {
                Direction faceDir = Direction.fromRotation(SeijaUtil.getYaw(hitVec));
                if (needDir == faceDir && BlockUtil.canPlaceIn(pos.offset(needDir))) {
                    return new PlaceData(pos.offset(clickDir),clickDir.getOpposite(),hitVec,true,null);
                }
            }
            //Vec3d hitVec = pos.toCenterPos().offset(clickDir, 0.5);

        }
        return new PlaceData(null,null,null,false,null);
    }

    @Override
    public boolean isSuitable(BlockState needState, BlockPos pos) {
        return needState.getBlock() instanceof BedBlock && needState.get(Properties.BED_PART) == BedPart.FOOT;
    }
}
