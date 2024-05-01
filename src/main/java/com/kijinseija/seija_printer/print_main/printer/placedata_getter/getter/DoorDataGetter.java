package com.kijinseija.seija_printer.print_main.printer.placedata_getter.getter;

import com.kijinseija.seija_printer.print_main.printer.placedata_getter.AbstractDataGetter;
import com.kijinseija.seija_printer.print_main.printer.util.BlockRotDataGetter;
import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
import com.kijinseija.seija_printer.print_main.printer.util.SeijaUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.RotationData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DoorDataGetter extends AbstractDataGetter {
    MinecraftClient mc = MinecraftClient.getInstance();
    @Override
    public PlaceData getData(BlockState needState, DirData dirData) {
        BlockPos pos = dirData.placePos();
        Direction needFac = needState.get(Properties.HORIZONTAL_FACING);
        DoorHinge needHinge = needState.get(Properties.DOOR_HINGE);
        RotationData rotData = BlockRotDataGetter.getRotData(needState);
        for (Direction clickDir : dirData.dirs()) {
           // Vec3d centerVec = pos.toCenterPos().offset(clickDir,0.5);
            //ArrayList<Vec3d> extendVec = BlockUtil.getExtendVec(clickDir, true);
            for (Vec3d hitVec : dirData.getClickVecs(clickDir)) {
                //Vec3d hitVec = centerVec.add(vec3d.multiply(0.3));
                Direction faceDir = Direction.fromRotation(SeijaUtil.getYaw(hitVec));
                if (faceDir!=needFac&&rotData==null)continue;
                DoorHinge hinge = getHinge(pos, clickDir, faceDir, needState.getBlock(), hitVec);
                if (hinge!=needHinge)continue;
                return new PlaceData(pos.offset(clickDir),clickDir.getOpposite(),hitVec,true,rotData);
            }
        }
        return new PlaceData(null,null,null,false,null);
    }
    private DoorHinge getHinge(BlockPos pos, Direction clickDir, Direction faceDir, Block door, Vec3d hitVec) {
        boolean bl2;//抄mc的改一下 clickdir是获取的dir 发包要取反
        World blockView = mc.world;
        BlockPos blockPos =pos.offset(clickDir);
        BlockPos blockPos2 = blockPos.up();
        Direction direction2 = faceDir.rotateYCounterclockwise();
        BlockPos blockPos3 = blockPos.offset(direction2);
        BlockState blockState = blockView.getBlockState(blockPos3);
        BlockPos blockPos4 = blockPos2.offset(direction2);
        BlockState blockState2 = blockView.getBlockState(blockPos4);
        Direction direction3 = faceDir.rotateYClockwise();
        BlockPos blockPos5 = blockPos.offset(direction3);
        BlockState blockState3 = blockView.getBlockState(blockPos5);
        BlockPos blockPos6 = blockPos2.offset(direction3);
        BlockState blockState4 = blockView.getBlockState(blockPos6);
        int i = (blockState.isFullCube(blockView, blockPos3) ? -1 : 0) + (blockState2.isFullCube(blockView, blockPos4) ? -1 : 0) + (blockState3.isFullCube(blockView, blockPos5) ? 1 : 0) + (blockState4.isFullCube(blockView, blockPos6) ? 1 : 0);
        boolean bl = blockState.isOf(door) && blockState.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER;
        boolean bl3 = bl2 = blockState3.isOf(door) && blockState3.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER;
        if (bl && !bl2 || i > 0) {
            return DoorHinge.RIGHT;
        }
        if (bl2 && !bl || i < 0) {
            return DoorHinge.LEFT;
        }
        int j = faceDir.getOffsetX();
        int k = faceDir.getOffsetZ();
        double d = hitVec.x - (double)blockPos.getX();
        double e = hitVec.z - (double)blockPos.getZ();
        return j < 0 && e < 0.5 || j > 0 && e > 0.5 || k < 0 && d > 0.5 || k > 0 && d < 0.5 ? DoorHinge.RIGHT : DoorHinge.LEFT;
    }

    @Override
    public boolean isSuitable(BlockState needState, BlockPos pos) {
        return needState.getBlock() instanceof DoorBlock&&needState.get(Properties.DOUBLE_BLOCK_HALF)== DoubleBlockHalf.LOWER;
    }
}
