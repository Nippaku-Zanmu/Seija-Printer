/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.block_fixer;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import com.kijinseija.seija_printer.settings.core.BoolSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractFixer {
    public Minecraft mc = Minecraft.getInstance();

    public AbstractFixer(String name) {
        bSetEnable = new BoolSetting.Builder().name(name).defaultValue(true).build();
    }
    public AbstractFixer( BoolSetting setting) {
        bSetEnable = setting;
    }


    public Printer pri(){
        return Printer.getINSTANCE();
    }

    public BoolSetting bSetEnable;

    /**
     * fix block进行方块修复
     *
     * @param pos pos 修复位置
     * @param needState needState需要的状态
     * @return {@link boolean}是否进行了修复
     */
    public abstract int fixBlock(BlockPos pos, BlockState needState);
    /**
     * need fix 检测是否需要修复
     *
     * @param pos pos 尝试修复的方块位置
     * @param needState needState 尝试修复的方块状态
     * @return {@link boolean}是否需要修复
     */
    public abstract boolean needFix(BlockPos pos, BlockState needState);
    public boolean isEnable(){return bSetEnable.get();}


    public static final int CONTINUE = 0;
    public static final int SUCCESS = 1;
    public static final int RETURN = 2;

}
