package com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer.state_decide;

import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class MainDecide {
    public static final MainDecide INSTANCE = new MainDecide();
    private final List<Decide> DECIDES = new ArrayList<>();
    private MainDecide() {
        DECIDES.add(new MultifaceGrowthDecide());
        DECIDES.add(new ChestDecide());
    }

    public static Property[] props = new Property[]{
        Properties.FACING, Properties.HORIZONTAL_FACING, Properties.BLOCK_HALF
        , Properties.DOOR_HINGE, Properties.AXIS, Properties.ATTACHMENT
        , Properties.HOPPER_FACING, Properties.ROTATION, Properties.WALL_MOUNT_LOCATION
        , Properties.CHEST_TYPE, Properties.SLAB_TYPE
    };

    public boolean test(BlockState needState, BlockState nowState, BlockPos placePos) {
        for (Decide decide : DECIDES) {
            if (decide.isSuit(needState,nowState,placePos)){
                return decide.test(needState, nowState,placePos);
            }
        }
        for (Property property : props) {
            Comparable now = null;
            Comparable need = null;

            try {
                now = nowState.get(property);
            } catch (IllegalArgumentException ignored) {
            }
            try {
                need = needState.get(property);
            } catch (IllegalArgumentException ignored) {
            }

            if (now != need) {
                // ChatUtils.sendMsg(Text.of("cont5"));
                return false;
            }
        }
        return true;
    }
}
