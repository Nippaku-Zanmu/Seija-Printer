package com.kijinseija.seija_printer.print_main.printer.placedata_getter.getter.HFaceDirOpposite;

import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SideShapeType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;


public class RedStoneGateDataGetter extends HFaceDirOppositeDataGetter {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    @Override
    public PlaceData getData(BlockState needState, DirData dirData) {
        BlockPos pos = dirData.placePos();
        if (mc.world.getBlockState(pos.down()).isSideSolid(mc.world, pos.down(), Direction.UP, SideShapeType.RIGID))
            return super.getData(needState, dirData);
        return PlaceData.NULL;
    }

    @Override
    public boolean isSuitable(BlockState needState, BlockPos pos) {
        return needState.getBlock() instanceof AbstractRedstoneGateBlock
            ;
    }
}
