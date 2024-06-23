package com.kijinseija.seija_printer.print_main.printer.util;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixininterface.IClientPlayerInteractionManager;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;



public class InvUtil {
    private final static MinecraftClient mc = MinecraftClient.getInstance();
    private final static Printer pri = Printer.getINSTANCE();

    private static void invSwitch(int slot, int hotbarSlot) {
        switch (pri.eSetInvSwitchMode.get()) {
            case SWAP:
                 invSwap(slot, hotbarSlot);
                 break;
            case PICK:
                 InvUtils.move().from(slot).to(hotbarSlot);
                 break;
        }
    }

    private static boolean invSwap(int slot, int hotBarSlot) {
        if (slot >= 0) {
            ScreenHandler handler = ((MinecraftClient) MeteorClient.mc).player.currentScreenHandler;
            Int2ObjectArrayMap stack = new Int2ObjectArrayMap();
            stack.put(slot, handler.getSlot(slot).getStack());
            MeteorClient.mc.getNetworkHandler().sendPacket((Packet) new ClickSlotC2SPacket(handler.syncId, handler.getRevision(), slot, hotBarSlot, SlotActionType.SWAP, mc.player.currentScreenHandler.getCursorStack().copy(), (Int2ObjectMap) stack));
            ((IClientPlayerInteractionManager) ((MinecraftClient) MeteorClient.mc).interactionManager).meteor$syncSelected();
            return true;
        }
        return false;
    }

    public static boolean switchBlock(Block b) {
        return switchItem(stack -> stack.getItem().equals(Item.BLOCK_ITEMS.get(b)));
    }

    public static boolean switchItem(Predicate<ItemStack> p) {
        FindItemResult resHot = InvUtils.findInHotbar(p);
        if (resHot.found()) {
            InvUtils.swap(resHot.slot(), false);
            return true;
        }

        FindItemResult res = InvUtils.find(p);
        if (!res.found()) return false;
//        invSwap(res.slot(), getInvSwapSlot());
        invSwitch(res.slot(),getInvSwapSlot());
        //InvUtils.move().from(res.slot()).to(mc.player.getInventory().selectedSlot);
        if (pri.bSetAntiWrongBlock.get() || pri.bSetIndirectInvSwap.get())
            return false;
        return true;
    }

    public static int getInvSwapSlot() {
        int selSlot = mc.player.getInventory().selectedSlot;
        if (pri.bSetIndirectInvSwap.get()) {
            return getSlot();
        }
        return selSlot;
    }

    //指针位置
    private static int i = 0;

    public static int getSlot() {
        List<Integer> usefulSlots = getUsefulSlots();
        int selSlot = mc.player.getInventory().selectedSlot;
        if (usefulSlots.size() > 1) usefulSlots.remove(Integer.valueOf(selSlot));
        if (usefulSlots.size() > 0) {
            if (i >= usefulSlots.size()) i = 0;
            return usefulSlots.get(i++);
        }
        return selSlot;
    }

    public static List<Integer> getUsefulSlots() {
        try {
            return getUsefulSlots(pri.sSetInvSwapSlot.get());
        } catch (Exception e) {
            pri.sSetInvSwapSlot.reset();
            ChatUtils.sendMsg(Text.of(e.getMessage()));
            ChatUtils.sendMsg(Text.of("检测到异常: 数据不符合格式 自动处理:已自动还原为默认数据"));
        }
        return getUsefulSlots(pri.sSetInvSwapSlot.getDefaultValue());
    }

    public static List<Integer> getUsefulSlots(String s) {
        String[] split = pri.sSetInvSwapSlot.get().split(",");
        final ArrayList<Integer> integers = new ArrayList<>();
        Arrays.stream(split).map(Integer::valueOf)
            .filter(i -> i >= 0 && i < 10)
            .forEach(integers::add);
        return integers;
    }

    public static boolean findBlock(Block b) {

        return findItem(stack -> stack.getItem().equals(Item.BLOCK_ITEMS.get(b)));
    }

    public static LinkedList<ItemStack> getBlockStacks(Block b) {
        final LinkedList<ItemStack> stacks = new LinkedList<>();
        findItem(stack -> {
            boolean equals = stack.getItem().equals(Item.BLOCK_ITEMS.get(b));
            if (equals) {
                stacks.add(stack);
            }
            return equals;
        });
        return stacks;
    }

    public static boolean findItem(Predicate<ItemStack> p) {
        return InvUtils.find(p).found();
    }
}
