/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.modules;

import com.kijinseija.seija_printer.print_main.printer.util.PredictUtility;
import com.kijinseija.seija_printer.print_main.printer.util.SeijaUtil;
import com.kijinseija.seija_printer.settings.impl.DirectionListSetting;
import com.kijinseija.seija_printer.settings.core.DoubleSetting;
import com.kijinseija.seija_printer.settings.core.Setting;
import com.kijinseija.seija_printer.settings.core.SettingGroup;
import com.kijinseija.seija_printer.utils.player.ChatUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import java.util.HashMap;
import java.util.List;

public class RayTraceTest extends ClientModule {

    public RayTraceTest() {
        super("RayTest", "");
        dirmap = new HashMap<>();
        dirmap.put(Direction.DOWN, new Vec3(-3.838728915128419E-18, -1.0, 1.2240450621459233E-16));
        dirmap.put(Direction.UP, new Vec3(0.0, 1.0, 0.0));
        dirmap.put(Direction.EAST, new Vec3(1.0, -0.0, 1.2246468525851679E-16));
        dirmap.put(Direction.NORTH, new Vec3(1.2246468525851679E-16, -0.0, -1.0));
        dirmap.put(Direction.SOUTH, new Vec3(0.0, -0.0, 1.0));
        dirmap.put(Direction.WEST, new Vec3(-1.0, -0.0, 0.0));

    }

    HashMap<Direction, Vec3> dirmap;
    SettingGroup sgDefault = settings.getDefaultGroup();
    Setting<Double> x = sgDefault.add(new DoubleSetting.Builder().name("x").build());
    Setting<Double> y = sgDefault.add(new DoubleSetting.Builder().name("y").build());
    Setting<Double> z = sgDefault.add(new DoubleSetting.Builder().name("z").build());

    Setting<List<Direction>> dirs = sgDefault.add(new DirectionListSetting.Builder().name("Dir").build());

    protected final Vec3 getRotationVector(float pitch, float yaw) {
        float f = pitch * ((float) Math.PI / 180);
        float g = -yaw * ((float) Math.PI / 180);
        float h = Mth.cos(g);
        float i = Mth.sin(g);
        float j = Mth.cos(f);
        float k = Mth.sin(f);
        return new Vec3(i * j, -k, h * j);
    }

    @Override
    public void onActivate() {
////        crosshairTarget
////        Vec3d vec3d = mc.player.getCameraPosVec(1);
////        Vec3d vec3d2 = mc.player.getRotationVec(1);
////        if (dirs.get().size() == 0) return;
//        Vec3d vec3d = new Vec3d(x.get(), y.get(), z.get());//眼睛位置
////        Vec3d vec3d2 = dirmap.get(dirs.get().get(0));
////        Vec3d vec3d3 = vec3d.add(vec3d2.x * 1, vec3d2.y * 1, vec3d2.z * 1);
//            //虚假转头
//        Vec3d vec3d2 = mc.cameraEntity.getRotationVec(1.0f);
//        Vec3d vec3d3 = vec3d.add(vec3d2.x * 5, vec3d2.y * 5, vec3d2.z * 5);
//        //实际转头
//        //乘数为触摸距离
//
////        // BlockHitResult raycast = mc.world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, false ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, mc.player));
////
////        Box box = mc.cameraEntity.getBoundingBox().stretch(vec3d2.multiply(mc.interactionManager.getReachDistance())).expand(1.0, 1.0, 1.0);
////        EntityHitResult entityHitResult = ProjectileUtil.raycast(mc.getCameraEntity(), mc.player.getEyePos(), vec3d3, box, entity -> !entity.isSpectator() && entity.canHit(), 25);
////
////
////        if (entityHitResult.getPos() != null)
////            ChatUtils.sendMsg(Text.of(entityHitResult.getPos() + ":::" + entityHitResult.getType().name()));
////        else  ChatUtils.sendMsg(Text.of( ":::" + entityHitResult.getType().name()));

        Entity entity2 = mc.getCameraEntity();
        if (entity2 == null) {
            return;
        }
        if (mc.level == null) {
            return;
        }

        //double d = mc.interactionManager.getReachDistance();
        double d = Printer.getINSTANCE().dSetPrintingRange.get();
        //121
        HitResult crosshairTarget = null;

        //crosshairTarget = entity2.raycast(d, 1, false);
        Vec3 vec3d = PredictUtility.getPredPlayerVec().relative(Direction.UP,SeijaUtil.getEyeHeight());
        Vec3 targetVec = new Vec3(x.get(),y.get(),z.get());
        Vec3 vec3d2 = getRotationVector((float) SeijaUtil.getPitch(targetVec), (float) SeijaUtil.getYaw(targetVec));//entity2.getRotationVec(1.0f);
        ChatUtils.sendMsg(Component.nullToEmpty("P:" +mc.getCameraEntity().getXRot()+","+ SeijaUtil.getPitch(targetVec)));
        ChatUtils.sendMsg(Component.nullToEmpty("Y:" +mc.getCameraEntity().getYRot()+","+ SeijaUtil.getYaw(targetVec)));
        Vec3 vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);

        crosshairTarget =  mc.level.clip(new ClipContext(vec3d, vec3d3, ClipContext.Block.OUTLINE, false ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, entity2));


        boolean bl = false;
        int i = 3;
        double e = d;
//        if (mc.interactionManager.hasExtendedReach()) {
//            d = e = 6.0;
//        } else {
//            if (e > 3.0) {
//                bl = true;
//            }
//            d = e;
//        }
        //121
        e *= e;

        if (crosshairTarget != null) {
            e = crosshairTarget.getLocation().distanceToSqr(vec3d);
        }


        AABB box = entity2.getBoundingBox().expandTowards(vec3d2.scale(d)).inflate(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(entity2, vec3d, vec3d3, box, entity -> !entity.isSpectator() && entity.isPickable(), e);
        if (entityHitResult != null) {
            Vec3 vec3d4 = entityHitResult.getLocation();
            double g = vec3d.distanceToSqr(vec3d4);
            if (bl && g > 9.0) {
                crosshairTarget = BlockHitResult.miss(vec3d4, Direction.getApproximateNearest(vec3d2.x, vec3d2.y, vec3d2.z), BlockPos.containing(vec3d4));
            } else if (g < e || crosshairTarget == null) {
                crosshairTarget = entityHitResult;
            }
        }
        if (crosshairTarget.getLocation() != null)
            ChatUtils.sendMsg(Component.nullToEmpty(crosshairTarget.getLocation() + ":::" + crosshairTarget.getType().name()));
    }

}
