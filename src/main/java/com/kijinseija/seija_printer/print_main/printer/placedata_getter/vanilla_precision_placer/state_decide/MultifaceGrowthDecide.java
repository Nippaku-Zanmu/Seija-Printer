package com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer.state_decide;

import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.block.MultifaceGrowthBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;


public class MultifaceGrowthDecide implements Decide {
    private final Property[] facProps = new Property[]{
        Properties.WEST, Properties.EAST, Properties.UP, Properties.DOWN
        , Properties.NORTH, Properties.SOUTH
    };

    @Override
    public boolean test(BlockState needState, BlockState nowState, BlockPos ignore) {
//        for (Property<?> property : needState.getProperties()) {
//            ChatUtils.sendMsg(Text.of(property.getName()+",,,"+property.getType()+",,,"+property.getClass().getName()));
//        }
        for (Property prop : facProps) {

            try {
                //ChatUtils.sendMsg(Text.of(prop.getName() + needState.get(prop) + "," + nowState.get(prop)));
                if ((needState.get(prop).equals(nowState.get(prop))) && (nowState.get(prop).equals(Boolean.TRUE))) {
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
        return needState.getBlock() instanceof MultifaceGrowthBlock && nowState.getBlock() instanceof MultifaceGrowthBlock;
    }
    @Override
    public Setting[] getSettings() {
        return new Setting[0];
    }
}
