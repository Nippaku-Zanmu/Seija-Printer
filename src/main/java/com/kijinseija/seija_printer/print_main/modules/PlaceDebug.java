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
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;

public class PlaceDebug extends Module {

    public PlaceDebug() {
        super(Addon.CATEGORY, "PlaceDebug", "Test");

    }
    @EventHandler
    public void onPacket(PacketEvent.Send send){
        if (send.packet instanceof PlayerInteractBlockC2SPacket) {
            PlayerInteractBlockC2SPacket packet = (PlayerInteractBlockC2SPacket) send.packet;
            BlockHitResult bhr = packet.getBlockHitResult();

            ChatUtils.sendMsg(Text.of(bhr.getBlockPos()+"  Side:"+bhr.getSide()+" Vec:"+bhr.getPos()+"Type:"+bhr.getType()));
        }
    }
}
