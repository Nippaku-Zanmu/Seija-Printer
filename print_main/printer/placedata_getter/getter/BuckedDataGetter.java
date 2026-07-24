/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.placedata_getter.getter;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import com.kijinseija.seija_printer.print_main.printer.placedata_getter.AbstractDataGetter;
import com.kijinseija.seija_printer.print_main.printer.placedata_getter.vanilla_precision_placer.BlockStateVerify;
import com.kijinseija.seija_printer.print_main.printer.util.RayTraceUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
import com.kijinseija.seija_printer.print_main.printer.util.records.RotationData;
import com.kijinseija.seija_printer.settings.core.BoolSetting;
import com.kijinseija.seija_printer.settings.core.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BuckedDataGetter extends AbstractDataGetter {
    private static final Minecraft mc = Minecraft.getInstance();

    @Override
    public PlaceData getData(BlockState needState, DirData dirData) {
        BlockPos placePos = dirData.placePos();
//        BlockState nowState = mc.world.getBlockState(placePos);
        for (Direction offsetDir : dirData.dirs()) {
            BlockState helpState = mc.level.getBlockState(placePos.relative(offsetDir));
            if (needState.getBlock().equals(Blocks.WATER) && helpState.getBlock() instanceof SimpleWaterloggedBlock && !helpState.getValue(BlockStateProperties.WATERLOGGED)) {
                continue;
            }
            for (Vec3 clickVec : dirData.clickVecs(offsetDir, new DirData.DirDataConfig().setRaytrace(true).setStrictVec(true))) {
                return new PlaceData(placePos.relative(offsetDir), offsetDir.getOpposite(), clickVec, true,
                    ( RotationData.fromVec(clickVec)), () -> {
                    BlockHitResult blockHitResult = RayTraceUtil.INSTANCE.rayHitRes
                        (mc.player.getEyePosition(),  RotationData.build(BlockStateVerify.sendYaw,
                                BlockStateVerify.sendPitch), false,
                            mc.player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE));
//                    ChatUtils.info(blockHitResult.getType() + "  " + blockHitResult.getBlockPos());
//                    ChatUtils.info();
                    return blockHitResult.getType().equals(HitResult.Type.BLOCK) && blockHitResult.getBlockPos().equals(placePos.relative(offsetDir));

                });
//                return  PlaceData.newInstance(placePos.offset(offsetDir), offsetDir.getOpposite(), clickVec, true
//                    , (Printer.getINSTANCE().bSetRotate.get() ? null : RotationData.fromVec(clickVec)));
            }
        }
        return PlaceData.NULL;
    }

    @Override
    public boolean isSuitable(BlockState needState, BlockPos pos) {
        Block block = needState.getBlock();
        BlockState worldState = mc.level.getBlockState(pos);
        return (((block instanceof LiquidBlock)
            && ((bSetEnableWater.get() && block.equals(Blocks.WATER))
            || (bSetEnableLava.get() && block.equals(Blocks.LAVA)))
            && (needState.getValue(BlockStateProperties.LEVEL) == 0))
//            ||((bSetEnablePowderSnow.get()
//            && block.equals(Blocks.POWDER_SNOW)))
        )
            && (worldState.getBlock() instanceof AirBlock//空气直接放
            || (worldState.getBlock().equals(block) && worldState.getValue(BlockStateProperties.LEVEL) > 0));//同液体非源头
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
