// Decompiled with: CFR 0.152
// Class Version: 17
package com.kijinseija.seija_printer.print_main.printer.util;


import com.kijinseija.seija_printer.print_main.modules.Printer;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class PredictUtility {
    static MinecraftClient mc = MinecraftClient.getInstance();
    public static PlayerEntity predictPlayer(PlayerEntity entity, int ticks) {
        if (ticks<=0)return entity;
        Vec3d posVec = new Vec3d(entity.getX(), entity.getY(), entity.getZ());
        double motionX = entity.getX() - entity.prevX;
        double motionY = entity.getY() - entity.prevY;
        double motionZ = entity.getZ() - entity.prevZ;
        for (int i = 0; i < ticks; ++i) {
            if (!mc.world.isAir(BlockPos.ofFloored(posVec.add(0.0, motionY, 0.0)))) {
                motionY = 0.0;
            }
            if (!mc.world.isAir(BlockPos.ofFloored(posVec.add(motionX, 0.0, 0.0))) || !mc.world.isAir(BlockPos.ofFloored(posVec.add(motionX, 1.0, 0.0)))) {
                motionX = 0.0;
            }
            if (!mc.world.isAir(BlockPos.ofFloored(posVec.add(0.0, 0.0, motionZ))) || !mc.world.isAir(BlockPos.ofFloored(posVec.add(0.0, 1.0, motionZ)))) {
                motionZ = 0.0;
            }
            posVec = posVec.add(motionX, motionY, motionZ);
        }
        return PredictUtility.equipAndReturn(entity, posVec);
    }

    public static Vec3d getPredPlayerVec(){
        return predictPlayerVec(mc.player, Printer.getINSTANCE().iSetPredTick.get());
    }
    public static Vec3d predictPlayerVec(PlayerEntity entity, int ticks) {
        //if (ticks<=0)return entity;
        Vec3d posVec = new Vec3d(entity.getX(), entity.getY(), entity.getZ());
        double motionX = entity.getX() - entity.prevX;
        double motionY = entity.getY() - entity.prevY;
        double motionZ = entity.getZ() - entity.prevZ;
        for (int i = 0; i < ticks; ++i) {
            if (!mc.world.isAir(BlockPos.ofFloored(posVec.add(0.0, motionY, 0.0)))) {
                motionY = 0.0;
            }
            if (!mc.world.isAir(BlockPos.ofFloored(posVec.add(motionX, 0.0, 0.0))) || !mc.world.isAir(BlockPos.ofFloored(posVec.add(motionX, 1.0, 0.0)))) {
                motionX = 0.0;
            }
            if (!mc.world.isAir(BlockPos.ofFloored(posVec.add(0.0, 0.0, motionZ))) || !mc.world.isAir(BlockPos.ofFloored(posVec.add(0.0, 1.0, motionZ)))) {
                motionZ = 0.0;
            }
            posVec = posVec.add(motionX, motionY, motionZ);
        }
        return posVec;
        //return PredictUtility.equipAndReturn(entity, posVec);
    }

    public static PlayerEntity equipAndReturn(PlayerEntity original, Vec3d posVec) {
        PlayerEntity copyEntity = new PlayerEntity(mc.world, original.getBlockPos(), original.getYaw(), new GameProfile(UUID.fromString("66123666-1234-5432-6666-667563866600"), "PredictEntity339")){

            public boolean isSpectator() {
                return false;
            }

            public boolean isCreative() {
                return false;
            }
        };
        copyEntity.setPosition(posVec);
        copyEntity.setHealth(original.getHealth());
        copyEntity.prevX = original.prevX;
        copyEntity.prevZ = original.prevZ;
        copyEntity.prevY = original.prevY;
        copyEntity.getInventory().clone(original.getInventory());
        copyEntity.setYaw(original.getYaw());
        copyEntity.setPitch(original.getPitch());
        copyEntity.prevYaw = original.prevYaw;
        copyEntity.headYaw = original.headYaw;
        copyEntity.bodyYaw = original.bodyYaw;
        copyEntity.prevHeadYaw = original.prevHeadYaw;
        copyEntity.prevPitch = original.prevPitch;
        for (StatusEffectInstance se : original.getStatusEffects()) {
            copyEntity.addStatusEffect(se);
        }
        return copyEntity;
    }

    public static Vec3d clone(Vec3d v) {
        return new Vec3d(v.getX(), v.getY(), v.getZ());
    }
}
