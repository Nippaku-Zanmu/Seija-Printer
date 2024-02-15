package com.kijinseija.seija_printer.printer.placedata_getter.getter;

import com.kijinseija.seija_printer.printer.Printer;
import com.kijinseija.seija_printer.printer.placedata_getter.AbstractDataGetter;
import com.kijinseija.seija_printer.printer.util.PlaceData;
import com.kijinseija.seija_printer.printer.util.RenderHelper;
import com.kijinseija.seija_printer.printer.util.SeijaUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class ChestDataGetter extends AbstractDataGetter {
    MinecraftClient mc = MinecraftClient.getInstance();

    @Override
    public PlaceData getData(BlockPos pos, BlockState needState, List<Direction> dirs) {
        Direction needDir = needState.get(Properties.HORIZONTAL_FACING);
        ChestType needType = needState.get(Properties.CHEST_TYPE);

        Direction neiOffset = getNeiOffset(needDir, needType);
        BlockState neiState = mc.world.getBlockState(pos.offset(neiOffset==null?Direction.UP:neiOffset));

        boolean b = neiOffset != null && neiState.getBlock().equals(needState.getBlock())
            && neiState.get(Properties.HORIZONTAL_FACING).equals(needDir);
        //邻近状态
        if (!b)
            needType = ChestType.SINGLE;
        //另外一般没放的话就设为单个进行计算


        for (Direction dir : dirs) {
            Vec3d clickVec = pos.toCenterPos().offset(dir, 0.5);
            if (needType == ChestType.SINGLE
                && (mc.world.getBlockState(pos.offset(dir)).getBlock() != needState.getBlock()
                || (mc.world.getBlockState(pos.offset(dir)).getBlock() != needState.getBlock()
                && mc.world.getBlockState(pos.offset(dir)).get(Properties.HORIZONTAL_FACING) != needDir))

                && needDir == Direction.fromRotation(SeijaUtil.getYaw(clickVec)).getOpposite()) {
                if (neiOffset != null) {
                    Printer.getINSTANCE().blackList.add(RenderHelper.getBlackInfo(pos.offset(neiOffset)));
                }

                return new PlaceData(pos.offset(dir), dir.getOpposite(), clickVec, true, null);
            } else if (needType != ChestType.SINGLE && neiOffset == dir)
                return new PlaceData(pos.offset(dir), dir.getOpposite(), clickVec, true, null);

//            PlaceData testData = new PlaceData(pos.offset(dir), dir.getOpposite(), clickVec, true, null);
//            CData cData = getCData(testData, pri.needBlockReplace(needState));
//            if (cData.chestType == needType&&cData.dir == needDir)return testData;
        }
        return new PlaceData(null, null, null, false, null);
    }

    @Override
    public boolean isSuitable(BlockState needState, BlockPos pos) {
        return needState.getBlock() instanceof ChestBlock;
    }

    //    public CData getCData(PlaceData data, Block pb) {
//        Direction direction3;
//        ChestType chestType = ChestType.SINGLE;
//        Direction hFac = Direction.fromRotation(SeijaUtil.getYaw(data.hitVec()));
//        Direction direction = hFac.getOpposite();
//
//        boolean bl = false;
//        Direction direction2 = data.dir();
//        if (direction2.getAxis().isHorizontal() && bl && (direction3 = this.getNeighborChestDirection(pb, data, direction2.getOpposite())) != null && direction3.getAxis() != direction2.getAxis()) {
//            direction = direction3;
//            ChestType chestType2 = chestType = direction.rotateYCounterclockwise() == direction2.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
//        }
//        if (chestType == ChestType.SINGLE && !bl) {
//            if (direction == this.getNeighborChestDirection(pb, data, direction.rotateYClockwise())) {
//                chestType = ChestType.LEFT;
//            } else if (direction == this.getNeighborChestDirection(pb, data, direction.rotateYCounterclockwise())) {
//                chestType = ChestType.RIGHT;
//            }
//        }
//        return new CData(chestType,direction);
//        //direction , ct
//    }
//    private record CData(ChestType chestType,Direction dir){}
//
//    @Nullable
//    private Direction getNeighborChestDirection(Block placeBlo, PlaceData data, Direction dir) {
//        BlockState blockState = mc.world.getBlockState(data.pos().offset(dir));
//        return blockState.isOf(placeBlo) && blockState.get(Properties.CHEST_TYPE) == ChestType.SINGLE ? blockState.get(Properties.HORIZONTAL_FACING) : null;
//    }
    public Direction getNeiOffset(Direction cDir, ChestType t) {
        switch (t) {
            case LEFT -> {
                return cDir.rotateYClockwise();
            }
            case RIGHT -> {
                return cDir.rotateYCounterclockwise();
            }
        }
        return null;
    }
}
