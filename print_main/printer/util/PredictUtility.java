/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

// Decompiled with: CFR 0.152
// Class Version: 17
package com.kijinseija.seija_printer.print_main.printer.util;


import com.kijinseija.seija_printer.print_main.modules.Printer;
import com.mojang.authlib.GameProfile;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

public class PredictUtility {
    static Minecraft mc = Minecraft.getInstance();
    public static Player predictPlayer(Player entity, int ticks) {
        if (ticks<=0)return entity;
        Vec3 posVec = new Vec3(entity.getX(), entity.getY(), entity.getZ());
        double motionX = entity.getX() - entity.xo;
        double motionY = entity.getY() - entity.yo;
        double motionZ = entity.getZ() - entity.zo;
        for (int i = 0; i < ticks; ++i) {
            if (!mc.level.isEmptyBlock(BlockPos.containing(posVec.add(0.0, motionY, 0.0)))) {
                motionY = 0.0;
            }
            if (!mc.level.isEmptyBlock(BlockPos.containing(posVec.add(motionX, 0.0, 0.0))) || !mc.level.isEmptyBlock(BlockPos.containing(posVec.add(motionX, 1.0, 0.0)))) {
                motionX = 0.0;
            }
            if (!mc.level.isEmptyBlock(BlockPos.containing(posVec.add(0.0, 0.0, motionZ))) || !mc.level.isEmptyBlock(BlockPos.containing(posVec.add(0.0, 1.0, motionZ)))) {
                motionZ = 0.0;
            }
            posVec = posVec.add(motionX, motionY, motionZ);
        }
        return PredictUtility.equipAndReturn(entity, posVec);
    }

    public static Vec3 getPredPlayerVec(){
        return predictPlayerVec(mc.player, Printer.getINSTANCE().iSetPredTick.get());
    }
    public static Vec3 predictPlayerVec(Player entity, int ticks) {
        //if (ticks<=0)return entity;
        Vec3 posVec = new Vec3(entity.getX(), entity.getY(), entity.getZ());
        double motionX = entity.getX() - entity.xo;
        double motionY = entity.getY() - entity.yo;
        double motionZ = entity.getZ() - entity.zo;
        for (int i = 0; i < ticks; ++i) {
            if (!mc.level.isEmptyBlock(BlockPos.containing(posVec.add(0.0, motionY, 0.0)))) {
                motionY = 0.0;
            }
            if (!mc.level.isEmptyBlock(BlockPos.containing(posVec.add(motionX, 0.0, 0.0))) || !mc.level.isEmptyBlock(BlockPos.containing(posVec.add(motionX, 1.0, 0.0)))) {
                motionX = 0.0;
            }
            if (!mc.level.isEmptyBlock(BlockPos.containing(posVec.add(0.0, 0.0, motionZ))) || !mc.level.isEmptyBlock(BlockPos.containing(posVec.add(0.0, 1.0, motionZ)))) {
                motionZ = 0.0;
            }
            posVec = posVec.add(motionX, motionY, motionZ);
        }
        return posVec;
        //return PredictUtility.equipAndReturn(entity, posVec);
    }

    public static Player equipAndReturn(Player original, Vec3 posVec) {
        Player copyEntity = new Player(mc.level, new GameProfile(UUID.fromString("66123666-1234-5432-6666-667563866600"), "PredictEntity339")){


            @Override
            public GameType gameMode() {
                return GameType.SURVIVAL;
            }
        };

        copyEntity.setPos(posVec);
        copyEntity.setHealth(original.getHealth());
        copyEntity.xo = original.xo;
        copyEntity.zo = original.zo;
        copyEntity.yo = original.yo;
        copyEntity.getInventory().replaceWith(original.getInventory());
        copyEntity.setYRot(original.getYRot());
        copyEntity.setXRot(original.getXRot());
        copyEntity.yRotO = original.yRotO;
        copyEntity.yHeadRot = original.yHeadRot;
        copyEntity.yBodyRot = original.yBodyRot;
        copyEntity.yHeadRotO = original.yHeadRotO;
        copyEntity.xRotO = original.xRotO;
        for (MobEffectInstance se : original.getActiveEffects()) {
            copyEntity.addEffect(se);
        }
        return copyEntity;
    }

    public static Vec3 clone(Vec3 v) {
        return new Vec3(v.x(), v.y(), v.z());
    }
}
