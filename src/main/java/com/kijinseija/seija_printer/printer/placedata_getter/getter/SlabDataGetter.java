package com.kijinseija.seija_printer.printer.placedata_getter.getter;

import com.kijinseija.seija_printer.printer.util.PlaceData;
import com.kijinseija.seija_printer.printer.placedata_getter.AbstractDataGetter;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class SlabDataGetter extends AbstractDataGetter {
    MinecraftClient mc = MinecraftClient.getInstance();
    @Override
    public PlaceData getData(BlockPos pos, BlockState needState, List<Direction> dirs) {
        SlabType slabType = needState.get(SlabBlock.TYPE);
        if (slabType==SlabType.DOUBLE){
            PlaceData data = getData(pos, needState.with(SlabBlock.TYPE, SlabType.BOTTOM), dirs);
            if (data.valid())return data;
            else return getData(pos, needState.with(SlabBlock.TYPE, SlabType.TOP), dirs);
        }
        ArrayList<Direction> dirs1 = new ArrayList<>(dirs);
        switch (slabType) {
            case BOTTOM -> dirs1.remove(Direction.UP);
            case TOP -> dirs1.remove(Direction.DOWN);
        }
        for (Direction direction : dirs1) {
            BlockState helperState = mc.world.getBlockState(pos.offset(direction));
            if (helperState.getBlock().equals(needState.getBlock())
                && (
                (helperState.get(SlabBlock.TYPE) != SlabType.DOUBLE
                    && helperState.get(SlabBlock.TYPE) != slabType
                    && direction.getAxis() != Direction.Axis.Y)
                    ||
                    (helperState.get(SlabBlock.TYPE) != SlabType.DOUBLE&&
                        helperState.get(SlabBlock.TYPE) == slabType
                        && direction.getAxis() == Direction.Axis.Y))
            ) continue;
            if (direction.getAxis() == Direction.Axis.Y)
                return new PlaceData(pos.offset(direction), direction.getOpposite()
                    , pos.toCenterPos().offset(direction, 0.5), true, null);
            return new PlaceData(pos.offset(direction), direction.getOpposite()
                , pos.toCenterPos().offset(direction, 0.5)
                .add(0, slabType == SlabType.BOTTOM ? -0.1 : 0.1, 0), true, null);
        }
        return new PlaceData(null, null, null, false, null);
    }


    @Override
    public boolean isSuitable(BlockState needState, BlockPos pos) {
        return needState.getBlock() instanceof SlabBlock;
    }
}
