package com.kijinseija.seija_printer.print_main.printer.placedata_getter.getter;

import com.kijinseija.seija_printer.print_main.printer.util.BlockRotDataGetter;
import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
import com.kijinseija.seija_printer.print_main.printer.placedata_getter.AbstractDataGetter;
import com.kijinseija.seija_printer.print_main.printer.util.SeijaUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.RotationData;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class PistonDataGetter extends AbstractDataGetter {
    @Override
    public PlaceData getData(BlockState needState, DirData dirData) {
        BlockPos pos = dirData.placePos();
        RotationData rotData = BlockRotDataGetter.getRotData(needState);
        for (Direction dir : dirData.dirs()) {
            for (Vec3d hitVec : dirData.clickVecs(dir)) {
                Direction opposite = SeijaUtil.getEntityFacingOrder((float) SeijaUtil.getYaw(hitVec), (float) SeijaUtil.getPitch(hitVec))[0].getOpposite();

                if (rotData!=null||opposite.toString().toLowerCase().equals(needState.get(FacingBlock.FACING).toString()))
                    return new PlaceData(pos.offset(dir), dir.getOpposite(), hitVec, true, rotData);
            }
        }
        return new PlaceData(null, null, null, false, null);
    }


    @Override
    public boolean isSuitable(BlockState needState, BlockPos pos) {
        return needState.getBlock() instanceof PistonBlock
            || needState.getBlock() instanceof DispenserBlock
            || needState.getBlock() instanceof DropperBlock
            || needState.getBlock() instanceof BarrelBlock;
    }
}
