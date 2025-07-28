package com.kijinseija.seija_printer.mixin;

import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonBlock.class)
public class PistonBlockMixin {
    @Inject(method = "getPlacementState",at = @At("RETURN"))
    public void a(ItemPlacementContext ctx, CallbackInfoReturnable<BlockState> cir){
        ChatUtils.sendMsg(Text.of(ctx.getHitPos().toString()));
        ChatUtils.sendMsg(ctx.getPlayer().getName());
        ChatUtils.sendMsg(Text.of(""+ctx.getPlayer().getYaw(1.0F)));
        ChatUtils.sendMsg(Text.of(""+ctx.getPlayerLookDirection()));
    }
}
