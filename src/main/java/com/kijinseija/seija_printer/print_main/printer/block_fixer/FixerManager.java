package com.kijinseija.seija_printer.print_main.printer.block_fixer;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import com.kijinseija.seija_printer.print_main.printer.block_fixer.fixers.*;
import com.kijinseija.seija_printer.print_main.printer.block_fixer.fixers.click_fixer.*;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class FixerManager {

    public static final FixerManager INSTANCE = new FixerManager();

    /**
     * do fix 进行方块修复
     *
     * @param pos       pos 尝试修复的位置
     * @param needState needState 需要的状态
     * @return {@link boolean} 是否进行了修复操作
     */
    public int doFix(BlockPos pos, BlockState needState) {
        if (Printer.getINSTANCE().bSetEnableBlockFixer.get())//是否启用了方块修复
            try {
                for (AbstractFixer fixer : fixers) {//遍历所有修复器
                    if (fixer.needFix(pos, needState)) {
                        //检测是否可以修复->是则进行修复
                        //若进行了则返回真
                        switch (fixer.fixBlock(pos, needState)){
                            case AbstractFixer.SUCCESS : {
                                return AbstractFixer.SUCCESS;
                            }
                            case AbstractFixer.CONTINUE : {
                                continue;
                            }
                            case AbstractFixer.RETURN : {
                                return AbstractFixer.RETURN;
                            }
                        }
                    }
                }
            } catch (IllegalArgumentException ignored) {
                //防小天才瞎几把玩方块替换
            }

        return AbstractFixer.CONTINUE;
    }

    private List<AbstractFixer> fixers = new ArrayList<>();

    private FixerManager() {
        //修复器注册表
        fixers.add(new SlabFixer());
        fixers.add(new DirtFixer());
        fixers.add(new DoorFixer());
        fixers.add(new DaylightDetectorFixer());
        fixers.add(new LeverFixer());
        fixers.add(new RSTComparatorFixer());
        fixers.add(new RSTRepeaterFixer());
        fixers.add(new FlowerPotFixer());
        fixers.add(new CampFireFixer());
        fixers.add(new NoteBlockFixer());
        fixers.add(new StrippedFixer());
    }
}
