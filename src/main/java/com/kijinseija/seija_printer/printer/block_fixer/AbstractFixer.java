package com.kijinseija.seija_printer.printer.block_fixer;

import com.kijinseija.seija_printer.printer.Printer;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractFixer {
    public MinecraftClient mc = MinecraftClient.getInstance();
    public Printer pri = Printer.INSTANCE;


    /**
     * fix block进行方块修复
     *
     * @param pos pos 修复位置
     * @param needState needState需要的状态
     * @return {@link boolean}是否进行了修复
     */
    public abstract boolean fixBlock(BlockPos pos, BlockState needState);
    /**
     * need fix 检测是否需要修复
     *
     * @param pos pos 尝试修复的方块位置
     * @param needState needState 尝试修复的方块状态
     * @return {@link boolean}是否需要修复
     */
    public abstract boolean needFix(BlockPos pos, BlockState needState);

}
