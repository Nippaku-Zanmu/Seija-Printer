/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.block_fixer;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import com.kijinseija.seija_printer.print_main.printer.block_fixer.fixers.*;
import com.kijinseija.seija_printer.print_main.printer.block_fixer.fixers.click_fixer.*;
import com.kijinseija.seija_printer.print_main.printer.extra_setting.ExtraSettingManager;
import com.kijinseija.seija_printer.print_main.printer.extra_setting.HasExtraSetting;
import com.kijinseija.seija_printer.settings.core.SettingGroup;
import com.kijinseija.seija_printer.settings.core.Settings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import java.util.ArrayList;
import java.util.List;

public class FixerManager implements HasExtraSetting {

    public static final FixerManager INSTANCE = new FixerManager();

    /**
     * do fix 进行方块修复
     *
     * @param pos       pos 尝试修复的位置
     * @param needState needState 需要的状态
     * @return {@link boolean} 是否进行了修复操作
     */
    public int doFix(BlockPos pos, BlockState needState) {
        try {
            for (AbstractFixer fixer : fixers) {//遍历所有修复器
                if (fixer.isEnable()&&fixer.needFix(pos, needState)) {
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
        regFixer();
    }

    private void regFixer(){
        //修复器注册表
        fixers.add(new ScaffoldFixer());
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
        fixers.add(new RedStoneFixer());
    }

    @Override
    public SettingGroup getSettingGroup(Settings sets) {
        SettingGroup blockFixer = sets.createGroup("BlockFixer");
        for (AbstractFixer fixer : fixers) {
            blockFixer.add(fixer.bSetEnable);
        }
        return blockFixer;
    }
}
