package com.kijinseija.seija_printer.print_main.printer.placedata_getter.getter;

import com.kijinseija.seija_printer.print_main.printer.placedata_getter.AbstractDataGetter;
import com.kijinseija.seija_printer.print_main.printer.util.BlockUtil;
import com.kijinseija.seija_printer.print_main.printer.util.DirData;
import com.kijinseija.seija_printer.print_main.printer.util.PlaceData;
import com.kijinseija.seija_printer.print_main.printer.util.SeijaUtil;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class AnvilDataGetter extends AbstractDataGetter {
    MinecraftClient mc = MinecraftClient.getInstance();
    @Override
    public PlaceData getData(BlockState needState, DirData dirData) {
        if (BlockUtil.canPlaceIn(dirData.placePos().offset(Direction.DOWN)))
            return new PlaceData(null,null,null,false,null);
        Direction needFac = needState.get(Properties.HORIZONTAL_FACING);
        for (Direction dir : dirData.dirs()) {
            for (Vec3d clickVec : dirData.clickVecs(dir)) {
                if (Direction.fromRotation(SeijaUtil.getYaw(clickVec)).rotateYClockwise()==needFac){
                    return new PlaceData(dirData.placePos().offset(dir),dir.getOpposite(),clickVec,true,null);
                }
            }

        }
        return new PlaceData(null,null,null,false,null);
    }

    @Override
    public boolean isSuitable(BlockState needState, BlockPos pos) {
        return needState.getBlock()instanceof AnvilBlock;
    }
}
