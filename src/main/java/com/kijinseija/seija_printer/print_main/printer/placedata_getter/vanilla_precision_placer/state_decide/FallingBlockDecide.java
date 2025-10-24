package com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer.state_decide;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public class FallingBlockDecide implements Decide {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private final Setting<Boolean> isEnable = new BoolSetting.Builder()
        .name("avoid-block-fall")
        .defaultValue(true)
        .build();

    @Override
    public boolean isSuit(BlockState needState, BlockState nowState, BlockPos placePos) {
        return isEnable.get() && needState.getBlock().equals(nowState.getBlock()) && needState.getBlock() instanceof FallingBlock;
    }

    @Override
    public boolean test(BlockState needState, BlockState nowState, BlockPos placePos) {
        return mc.world != null && !FallingBlock.canFallThrough(mc.world.getBlockState(placePos.down()))
            && MainDecide.defaultTest(needState, nowState, placePos);
    }

    @Override
    public Setting<?>[] getSettings() {
        return new Setting[]{isEnable};
    }
}
