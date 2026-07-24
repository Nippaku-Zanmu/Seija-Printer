/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.placedata_getter;

import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
import com.kijinseija.seija_printer.print_main.modules.Printer;
import com.kijinseija.seija_printer.settings.core.Setting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

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
    public abstract PlaceData getData(BlockState needState, DirData dirData);
    /**
     * is suitable 判断获取器是否适用于这个方块状态
     *
     * @param needState needState 需要的方块状态
     * @param pos 方块位置
     * @return {@link boolean}是否适用于此精准放置数据获取器
     */
    public abstract boolean isSuitable(BlockState needState,BlockPos pos);
    public Setting[] getSettings(){
        return new Setting[]{};
    };
}
