package com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer.state_decide;

import meteordevelopment.meteorclient.settings.Setting;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;

public class SlabDecide implements Decide {
    @Override
    public boolean isSuit(BlockState needState, BlockState nowState, BlockPos placePos) {
        return needState.getBlock().equals(nowState.getBlock()) && needState.getBlock() instanceof SlabBlock;
    }

    @Override
    public boolean test(BlockState needState, BlockState nowState, BlockPos placePos) {
        if (needState.get(Properties.SLAB_TYPE)== SlabType.DOUBLE){
            return true;
        }else {
            return needState.get(Properties.SLAB_TYPE) == nowState.get(Properties.SLAB_TYPE);
        }
    }
    @Override
    public Setting[] getSettings() {
        return new Setting[0];
    }
}
