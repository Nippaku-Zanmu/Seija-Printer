package com.kijinseija.seija_printer.print_main.printer.util;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;

import java.util.*;

/**
 *  sche verify mixin util
 *  用于Mixin投影 让方块替换不爆红
 */
public class ScheVerifyMixinUtil {
//    public static boolean isReplacedStateEqual(BlockState sche, BlockState client){
//        return isReplacedBlockEqual(sche.getBlock(), client.getBlock())&& propEqual(sche.getProperties(),client.getProperties());
//    }
    //判断方块是否符合需求
    public static boolean isReplacedBlockEqual(Block sche, Block client) {
        List<Block> blocks = Printer.getINSTANCE().replaceMap.get(sche);
        //if (blocks!=null)return blocks.contains(client);
        return sche.equals(client) || (blocks != null && blocks.contains(client));
        //实际与投影相等或者在投影中方块有代替方案且实际方块符合代替方案
    }
    //比较2方块的Property是否相等
    public static boolean propEqual(BlockState sche, BlockState client) {
        Collection<Property<?>> c1 = sche.getProperties();
        Collection<Property<?>> c2 = client.getProperties();
        if (c1.size()!=c2.size())return false;
        for (Property<?> property : c1) {
            Comparable now = null;
            Comparable need = null;
            try {
                now = client.get(property);
            } catch (IllegalArgumentException ignored) {
            }
            try {
                need = sche.get(property);
            } catch (IllegalArgumentException ignored) {
            }
            if (now != need) {
                return false;
            }
        }
        return true;
    }
}
