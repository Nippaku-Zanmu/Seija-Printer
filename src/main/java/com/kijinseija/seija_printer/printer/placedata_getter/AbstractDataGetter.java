package com.kijinseija.seija_printer.printer.placedata_getter;

import com.kijinseija.seija_printer.printer.util.PlaceData;
import com.kijinseija.seija_printer.printer.Printer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;

public abstract class AbstractDataGetter {
    public Printer pri = Printer.getINSTANCE();
    /**
     * get data 获取方块放置数据
     *
     * @param pos pos 将要尝试放置的坐标
     * @param needState needState 需要的方块的状态
     * @param dirs dirs 所有可用的支持方向
     * @return {@link PlaceData}返回放置数据
     * @see PlaceData
     */
    public abstract PlaceData getData(BlockPos pos, BlockState needState, List<Direction> dirs);
    /**
     * is suitable 判断获取器是否适用于这个方块状态
     *
     * @param needState needState 需要的方块状态
     * @param pos 方块位置
     * @return {@link boolean}是否适用于此精准放置数据获取器
     */
    public abstract boolean isSuitable(BlockState needState,BlockPos pos);
}
