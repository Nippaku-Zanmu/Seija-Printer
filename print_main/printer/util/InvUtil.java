/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.util;

import com.kijinseija.seija_printer.print_main.modules.Printer;

import com.kijinseija.seija_printer.utils.player.ChatUtils;
import com.kijinseija.seija_printer.utils.player.FindItemResult;
import com.kijinseija.seija_printer.utils.player.InvUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;


public class InvUtil {
    private final static Minecraft mc = Minecraft.getInstance();
    private final static Printer pri = Printer.getINSTANCE();

    private static void invSwitch(int slot, int hotbarSlot) {
        switch (pri.eSetInvSwitchMode.get()) {
            case SWAP:
//                invSwap(slot, hotbarSlot);
                InvUtils.quickSwap().fromId(hotbarSlot).to(slot);
                break;
            case PICK:
                InvUtils.move().from(slot).to(hotbarSlot);
                break;
        }
    }

    public static boolean switchBlock(Block b) {
        return switchItem(stack -> stack.getItem().equals(getItemFormBlock(b)),b);
    }
    public static boolean switchItem(Predicate<ItemStack> p){
        return switchItem(p,null);
    }

    public static boolean switchItem(Predicate<ItemStack> p,Block b) {
        FindItemResult resHot = InvUtils.findInHotbar(p);
        if (resHot.found()) {
            InvUtils.swap(resHot.slot(), false);
            if ((!pri.bSetAntiWrongBlock.get()) || p.test(mc.player.getMainHandItem()))
                return true;
            return false;
        }
        if (isCreativeMode()&&b!=null) {

           // mc.player.getInventory().(mc.player.getStackInHand(Hand.MAIN_HAND));
            mc.gameMode.handleCreativeModeItemAdd(new ItemStack(b,1),
                36 + getSlot());

        } else {
            FindItemResult res = InvUtils.find(p);
            if (!res.found()) return false;
//        invSwap(res.slot(), getInvSwapSlot());
            invSwitch(res.slot(), getInvSwapSlot());
            //InvUtils.move().from(res.slot()).to(mc.player.getInventory().selectedSlot);

        }
        if (pri.bSetAntiWrongBlock.get() || pri.bSetIndirectInvSwap.get())
            return false;
        return true;
    }

    public static int getInvSwapSlot() {
        int selSlot = mc.player.getInventory().getSelectedSlot();
        if (pri.bSetIndirectInvSwap.get()) {
            return getSlot();
        }
        return selSlot;
    }

    //指针位置
    private static int i = 0;

    public static int getSlot() {
        List<Integer> usefulSlots = getUsefulSlots();
        int selSlot = mc.player.getInventory().getSelectedSlot();
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
            ChatUtils.sendMsg(Component.nullToEmpty(e.getMessage()));
            ChatUtils.sendMsg(Component.nullToEmpty("检测到异常: 数据不符合格式 自动处理:已自动还原为默认数据"));
        }
        return getUsefulSlots(pri.sSetInvSwapSlot.defaultValue);
    }

    public static List<Integer> getUsefulSlots(String s) {
        String[] split = (s == null ? "" : s).split(",");
        final ArrayList<Integer> integers = new ArrayList<>();
        Arrays.stream(split).map(Integer::valueOf)
            .filter(i -> i >= 0 && i < 10)
            .forEach(integers::add);
        return integers;
    }

    public static boolean findBlock(Block b) {

        return findItem(stack -> stack.getItem().equals(getItemFormBlock(b))) || isCreativeMode();
    }

    public static LinkedList<ItemStack> getBlockStacks(Block b) {
        final LinkedList<ItemStack> stacks = new LinkedList<>();
        if (isCreativeMode()){
            stacks.add(new ItemStack(b,1));
            return stacks;
        }
        findItem(stack -> {
            boolean equals = stack.getItem().equals(getItemFormBlock(b));
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

    public static Item getItemFormBlock(Block b) {
        if (b.equals(Blocks.WATER)) return Items.WATER_BUCKET;
        if (b.equals(Blocks.LAVA)) return Items.LAVA_BUCKET;
        if (b.equals(Blocks.POWDER_SNOW)) return Items.POWDER_SNOW_BUCKET;
        return Item.BY_BLOCK.get(b);
    }

    public static boolean isCreativeMode() {
        return mc.player.getAbilities().instabuild;
    }
}
