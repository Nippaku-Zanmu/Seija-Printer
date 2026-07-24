/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer.state_decide;

import com.kijinseija.seija_printer.settings.core.Setting;
import com.kijinseija.seija_printer.utils.player.ChatUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.MultifaceSpreadeableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;


public class MultifaceGrowthDecide implements Decide {
    private final Property[] facProps = new Property[]{
        BlockStateProperties.WEST, BlockStateProperties.EAST, BlockStateProperties.UP, BlockStateProperties.DOWN
        , BlockStateProperties.NORTH, BlockStateProperties.SOUTH
    };

    @Override
    public boolean test(BlockState needState, BlockState nowState, BlockPos ignore) {
//        for (Property<?> property : needState.getProperties()) {
//            ChatUtils.sendMsg(Text.of(property.getName()+",,,"+property.getType()+",,,"+property.getClass().getName()));
//        }
        for (Property prop : facProps) {

            try {
                //ChatUtils.sendMsg(Text.of(prop.getName() + needState.get(prop) + "," + nowState.get(prop)));
                if ((needState.getValue(prop).equals(nowState.getValue(prop))) && (nowState.getValue(prop).equals(Boolean.TRUE))) {
                    //ChatUtils.sendMsg(Text.of("Ret"+prop.getName() + needState.get(prop) + "," + nowState.get(prop)));
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }



    @Override
    public boolean isSuit(BlockState needState, BlockState nowState, BlockPos ignore) {
        return needState.getBlock() instanceof MultifaceSpreadeableBlock && nowState.getBlock() instanceof MultifaceSpreadeableBlock;
    }
    @Override
    public Setting[] getSettings() {
        return new Setting[0];
    }
}
