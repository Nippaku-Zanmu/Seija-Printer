package com.kijinseija.seija_printer.print_main.modules;

import com.kijinseija.seija_printer.Addon;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.text.Text;

public class SwapTest extends Module {
    public SwapTest() {
        super(Addon.CATEGORY, "SwapTest", "");
    }
    @EventHandler
    public void onPacket(PacketEvent.Send e){
        if (e.packet instanceof ClickSlotC2SPacket packet) {
            ChatUtils.sendMsg(Text.of("Slot"+packet.getSlot()+"Button:"+packet.getButton()));
            ChatUtils.sendMsg(Text.of(packet.getActionType()+"  "+packet.getStack().getItem()));
        }
    }

}
