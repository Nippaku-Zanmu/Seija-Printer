/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer.state_decide;

import com.kijinseija.seija_printer.print_main.printer.extra_setting.HasExtraSetting;
import com.kijinseija.seija_printer.settings.core.Setting;
import com.kijinseija.seija_printer.settings.core.SettingGroup;
import com.kijinseija.seija_printer.settings.core.Settings;
import com.kijinseija.seija_printer.utils.player.ChatUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.ArrayList;
import java.util.List;

public class MainDecide implements HasExtraSetting {
    public static final MainDecide INSTANCE = new MainDecide();
    private final List<Decide> DECIDES = new ArrayList<>();

    private MainDecide() {
        DECIDES.add(new MultifaceGrowthDecide());
        DECIDES.add(new ChestDecide());
        DECIDES.add(new SlabDecide());
        DECIDES.add(new FallingBlockDecide());
    }

    public static Property[] props = new Property[]{
        BlockStateProperties.FACING, BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF
        , BlockStateProperties.DOOR_HINGE, BlockStateProperties.AXIS, BlockStateProperties.BELL_ATTACHMENT
        , BlockStateProperties.FACING_HOPPER, BlockStateProperties.ROTATION_16, BlockStateProperties.ATTACH_FACE
        , BlockStateProperties.CHEST_TYPE, BlockStateProperties.SLAB_TYPE, BlockStateProperties.ORIENTATION
    };

    public boolean test(BlockState needState, BlockState nowState, BlockPos placePos) {
        for (Decide decide : DECIDES) {
            if (decide.isSuit(needState, nowState, placePos)) {
                return decide.test(needState, nowState, placePos);
            }
        }
        return defaultTest(needState, nowState, placePos);
//        for (Property property : props) {
//            Comparable now = null;
//            Comparable need = null;
//
//            try {
//                now = nowState.get(property);
//            } catch (IllegalArgumentException ignored) {
//            }
//            try {
//                need = needState.get(property);
//            } catch (IllegalArgumentException ignored) {
//            }
//
//            if (now != need) {
////                ChatUtils.sendMsg(Text.of("Now:"+now+"need:"+need));
////                for (Property<?> nowStateProperty : nowState.getProperties()) {
////                    ChatUtils.sendMsg(Text.of(nowStateProperty+" : "+needState.get(nowStateProperty)));
////                }
//                return false;
//            }
//        }
//        return true;
    }

    public static boolean defaultTest(BlockState needState, BlockState nowState, BlockPos placePos) {
        for (Property property : props) {
            Comparable now = null;
            Comparable need = null;

            try {
                now = nowState.getValue(property);
            } catch (IllegalArgumentException ignored) {
            }
            try {
                need = needState.getValue(property);
            } catch (IllegalArgumentException ignored) {
            }

            if (now != need) {
//                ChatUtils.sendMsg(Text.of("Now:"+now+"need:"+need));
//                for (Property<?> nowStateProperty : nowState.getProperties()) {
//                    ChatUtils.sendMsg(Text.of(nowStateProperty+" : "+needState.get(nowStateProperty)));
//                }
                return false;
            }
        }
        return true;
    }

    @Override
    public SettingGroup getSettingGroup(Settings settings) {
        SettingGroup sgDecide = settings.createGroup("BlockDecide");
        for (Decide decide : DECIDES) {
            for (Setting<?> setting : decide.getSettings()) {
                sgDecide.add(setting);
            }
        }
        return sgDecide;
    }
}
