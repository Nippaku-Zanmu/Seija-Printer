package com.kijinseija.seija_printer.print_main.printer.util;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.LinkedList;
import java.util.function.Predicate;

public class InvUtil {
    private final static MinecraftClient mc = MinecraftClient.getInstance();
    private final static Printer pri = Printer.getINSTANCE();

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
        InvUtils.move().from(res.slot()).to(mc.player.getInventory().selectedSlot);
        if (pri.antiWrongBlock.isVisible())
            return false;
        return true;
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
