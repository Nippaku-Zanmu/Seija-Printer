package com.kijinseija.seija_printer.print_main.modules;

import com.kijinseija.seija_printer.Addon;

import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.SideShapeType;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class SideTest extends Module {
    public SideTest() {
        super(Addon.CATEGORY,"SideTest","");
    }

    @Override
    public void onActivate() {
        HitResult crosshairTarget = mc.crosshairTarget;
        if (crosshairTarget instanceof BlockHitResult) {
            BlockPos blockPos = ((BlockHitResult) crosshairTarget).getBlockPos();
            for (Direction dir : Direction.values()) {
                BlockState blockState = mc.world.getBlockState(blockPos);
                ChatUtils.sendMsg(Text.of(dir.name()+"::"+blockState.isSideSolid(mc.world,blockPos,dir, SideShapeType.FULL)));
            }
        }
    }
}
