package com.kijinseija.seija_printer.mixin;

import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientWorld.class)
public interface ClientWorldAccessor {
    @Accessor("pendingUpdateManager")
    PendingUpdateManager getPendingUpdateManager();
//    @Invoker("getPendingUpdateManager")
//    PendingUpdateManager getPendingUpdateManager();
}
