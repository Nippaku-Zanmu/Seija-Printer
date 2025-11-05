/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer.state_decide;

import com.kijinseija.seija_printer.print_main.printer.extra_setting.HasExtraSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

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
        Properties.FACING, Properties.HORIZONTAL_FACING, Properties.BLOCK_HALF
        , Properties.DOOR_HINGE, Properties.AXIS, Properties.ATTACHMENT
        , Properties.HOPPER_FACING, Properties.ROTATION, Properties.BLOCK_FACE
        , Properties.CHEST_TYPE, Properties.SLAB_TYPE, Properties.ORIENTATION
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
                now = nowState.get(property);
            } catch (IllegalArgumentException ignored) {
            }
            try {
                need = needState.get(property);
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
