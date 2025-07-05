package com.kijinseija.seija_printer.print_main.printer.placedata_getter.getter;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import com.kijinseija.seija_printer.print_main.printer.placedata_getter.AbstractDataGetter;
import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
import com.kijinseija.seija_printer.print_main.printer.util.records.RotationData;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class BuckedDataGetter extends AbstractDataGetter {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    @Override
    public PlaceData getData(BlockState needState, DirData dirData) {
        BlockPos placePos = dirData.placePos();
//        BlockState nowState = mc.world.getBlockState(placePos);
        for (Direction offsetDir : dirData.dirs()) {
            BlockState helpState = mc.world.getBlockState(placePos.offset(offsetDir));
            if (needState.getBlock().equals(Blocks.WATER) && helpState.getBlock() instanceof Waterloggable && !helpState.get(Properties.WATERLOGGED)) {
                continue;
            }
            for (Vec3d clickVec : dirData.clickVecs(offsetDir, new DirData.DirDataConfig().setRaytrace(true).setStrictVec(true))) {

                return  PlaceData.newInstance(placePos.offset(offsetDir), offsetDir.getOpposite(), clickVec, true
                    , (Printer.getINSTANCE().bSetRotate.get() ? null : RotationData.fromVec(clickVec)));
            }
        }
        return PlaceData.NULL;
    }

    @Override
    public boolean isSuitable(BlockState needState, BlockPos pos) {
        Block block = needState.getBlock();
        BlockState worldState = mc.world.getBlockState(pos);
        return (((block instanceof FluidBlock)
            && ((bSetEnableWater.get() && block.equals(Blocks.WATER))
            || (bSetEnableLava.get() && block.equals(Blocks.LAVA)))
            && (needState.get(Properties.LEVEL_15) == 0))
//            ||((bSetEnablePowderSnow.get()
//            && block.equals(Blocks.POWDER_SNOW)))
        )
            && (worldState.getBlock() instanceof AirBlock//空气直接放
            || (worldState.getBlock().equals(block) && worldState.get(Properties.LEVEL_15) > 0));//同液体非源头
    }

    private final Setting<Boolean> bSetEnableLava = new BoolSetting.Builder().name("EnableLavaPlace")
        .build();
    private final Setting<Boolean> bSetEnableWater = new BoolSetting.Builder().name("EnableWaterPlace").build();
    //private final Setting<Boolean> bSetEnablePowderSnow = new BoolSetting.Builder().name("EnablePowderSnow").build();

    @Override
    public Setting[] getSettings() {
        return new Setting[]{bSetEnableLava, bSetEnableWater};
    }
}
