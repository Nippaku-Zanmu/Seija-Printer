/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.modules;

import com.kijinseija.seija_printer.utils.player.ChatUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.phys.BlockHitResult;

public class PlaceDebug extends ClientModule {

    public PlaceDebug() {
        super("PlaceDebug", "Test");

    }
    public void onPacket(ServerboundUseItemOnPacket packet){
        if (packet != null) {
            BlockHitResult bhr = packet.getHitResult();

            ChatUtils.sendMsg(Component.nullToEmpty(bhr.getBlockPos()+"  Side:"+bhr.getDirection()+" Vec:"+bhr.getLocation()+"Type:"+bhr.getType()));
        }
    }
}
