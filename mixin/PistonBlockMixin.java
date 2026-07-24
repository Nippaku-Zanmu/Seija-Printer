/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.mixin;

import com.kijinseija.seija_printer.utils.player.ChatUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonBaseBlock.class)
public class PistonBlockMixin {
    @Inject(method = "getStateForPlacement",at = @At("RETURN"))
    public void a(BlockPlaceContext ctx, CallbackInfoReturnable<BlockState> cir){
        ChatUtils.sendMsg(Component.nullToEmpty(ctx.getClickLocation().toString()));
        ChatUtils.sendMsg(ctx.getPlayer().getName());
        ChatUtils.sendMsg(Component.nullToEmpty(""+ctx.getPlayer().getViewYRot(1.0F)));
        ChatUtils.sendMsg(Component.nullToEmpty(""+ctx.getNearestLookingDirection()));
    }
}
