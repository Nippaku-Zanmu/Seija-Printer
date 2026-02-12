/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.modules;

import com.kijinseija.seija_printer.Addon;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.phys.BlockHitResult;

public class PlaceDebug extends Module {

    public PlaceDebug() {
        super(Addon.CATEGORY, "PlaceDebug", "Test");

    }
    @EventHandler
    public void onPacket(PacketEvent.Send send){
        if (send.packet instanceof ServerboundUseItemOnPacket) {
            ServerboundUseItemOnPacket packet = (ServerboundUseItemOnPacket) send.packet;
            BlockHitResult bhr = packet.getHitResult();

            ChatUtils.sendMsg(Component.nullToEmpty(bhr.getBlockPos()+"  Side:"+bhr.getDirection()+" Vec:"+bhr.getLocation()+"Type:"+bhr.getType()));
        }
    }
}
