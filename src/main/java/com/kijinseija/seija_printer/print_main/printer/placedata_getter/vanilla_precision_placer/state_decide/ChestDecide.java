package com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer.state_decide;

import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ChestDecide implements Decide{
    private static final  MinecraftClient mc = MinecraftClient.getInstance();
    @Override
    public boolean isSuit(BlockState needState, BlockState nowState, BlockPos placePos) {
        return needState.getBlock().equals(nowState.getBlock())&&(
            needState.getBlock() instanceof ChestBlock);
    }

    @Override
    public boolean test(BlockState needState, BlockState nowState, BlockPos placePos) {
        if (!needState.get(Properties.HORIZONTAL_FACING).equals(nowState.get(Properties.HORIZONTAL_FACING)))
        {
            return false;
        }//方位判断
        ChestType needType = needState.get(Properties.CHEST_TYPE);
        ChestType nowType = nowState.get(Properties.CHEST_TYPE);
        if ((!needType.equals(nowType))&&nowType.equals(ChestType.SINGLE)){
            //特殊情况 旁边的箱子可能没放
            Direction chestDir = nowState.get(Properties.HORIZONTAL_FACING);
            Direction neiOffset = getNeiOffset(chestDir, needType);
            BlockPos neiPos = placePos.offset(neiOffset);
            //基本数据
            BlockState neiState = mc.world.getBlockState(neiPos);
            //旁边不是箱子或者旁边是箱子但是方向错误
            return !neiState.getBlock().equals(needState.getBlock())
                || !neiState.get(Properties.HORIZONTAL_FACING).equals(chestDir);

        }
        return needType.equals(nowType);
    }
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
