/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.util;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import com.kijinseija.seija_printer.print_main.printer.util.records.RotationData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class RayTraceUtil {
    private static final Printer pri = Printer.getINSTANCE();
    public static RayTraceUtil INSTANCE = new RayTraceUtil();

    private RayTraceUtil() {
        dirmap = new HashMap<>();
        dirmap.put(Direction.DOWN,new Vec3(-3.838728915128419E-18, -1.0, 1.2240450621459233E-16));
        dirmap.put(Direction.UP,new Vec3(0.0, 1.0, 0.0));
        dirmap.put(Direction.EAST,new Vec3(1.0, -0.0, 1.2246468525851679E-16));
        dirmap.put(Direction.NORTH,new Vec3(1.2246468525851679E-16, -0.0, -1.0));
        dirmap.put(Direction.SOUTH,new Vec3(0.0, -0.0, 1.0));
        dirmap.put(Direction.WEST,new Vec3(-1.0, -0.0, 0.0));

    }
    private final HashMap<Direction,Vec3> dirmap;
    private final static Minecraft mc = Minecraft.getInstance();
    /**
     * get strict vec
     *
     * @param vec3d vec 初始粗略的点击点(可能不在支持方块的表面)
     * @param rayDir rayDir 射线计算方向
     * @param includeFluids includeFluids 计算流体
     * @return {@link BlockHitResult}
     * @see BlockHitResult
     */
    public BlockHitResult getStrictVecResult(Vec3 vec3d, Direction rayDir, boolean includeFluids,double rayLength){
        Vec3 vec3d2 = dirmap.get(rayDir);
        Vec3 vec3d3 = vec3d.add(vec3d2.x * rayLength, vec3d2.y * rayLength, vec3d2.z * rayLength);
        return mc.level.clip(new ClipContext(vec3d, vec3d3, ClipContext.Block.OUTLINE, includeFluids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, mc.player));
    }

    public static  Vec3 getRotationVector(float pitch, float yaw) {
        float f = pitch * ((float) Math.PI / 180);
        float g = -yaw * ((float) Math.PI / 180);
        float h = Mth.cos(g);
        float i = Mth.sin(g);
        float j = Mth.cos(f);
        float k = Mth.sin(f);
        return new Vec3(i * j, -k, h * j);
    }
    public boolean rayTrace(@Nullable BlockPos placePos, @Nullable Direction offsetDir, Vec3 target){
        return rayTrace(placePos,offsetDir, target,pri.bSetRayTrace.isVisible()&&pri.bSetRayTrace.get(),pri.bSetIgnoreEntity.get(), pri.dSetPrintingRange.get());
    }
    public boolean rayTrace(@Nullable BlockPos interactPos,@Nullable Direction clickDir, Vec3 target, boolean raytrace, boolean ignoreEntity, double rayRange){
        if (!(raytrace))return true;

        Entity entity2 = mc.player;
        if (entity2 == null) {
            return false;
        }
        if (mc.level == null) {
            return false;
        }

        double d =rayRange;
        HitResult crosshairTarget = null;

        //crosshairTarget = entity2.raycast(d, 1, false);
        Vec3 vec3d = PredictUtility.getPredPlayerVec().relative(Direction.UP,SeijaUtil.getEyeHeight());

        Vec3 vec3d2 = getRotationVector((float) SeijaUtil.getPitch(target), (float) SeijaUtil.getYaw(target));//entity2.getRotationVec(1.0f);
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


        if (!ignoreEntity){
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
        }

        if (crosshairTarget == null)return false;
        if (crosshairTarget.getType()!= HitResult.Type.BLOCK)return false;
        //ChatUtils.sendMsg(Text.of("pos:"+ crosshairTarget.getPos()+ " dis: "+target.distanceTo(crosshairTarget.getPos())));
        if (clickDir==null||interactPos==null)
            return  target.distanceTo(crosshairTarget.getLocation())<=0.1;
        return target.distanceTo(crosshairTarget.getLocation())<=0.1
            &&(!(crosshairTarget instanceof BlockHitResult bhr0) || bhr0.getDirection() == clickDir)
            &&(!(crosshairTarget instanceof BlockHitResult bhr) ||interactPos.equals(bhr.getBlockPos()));
    }
    public BlockHitResult rayHitRes(Vec3 start, RotationData rotate, boolean ignoreEntity, double rayRange){


        Entity entity2 = mc.player;
        if (entity2 == null) {
            return null;
        }
        if (mc.level == null) {
            return null;
        }

        double d =rayRange;
        HitResult crosshairTarget = null;

        //crosshairTarget = entity2.raycast(d, 1, false);

        Vec3 vec3d2 = getRotationVector((float) rotate.pitch(), (float) rotate.yaw());//entity2.getRotationVec(1.0f);
        Vec3 vec3d3 = start.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);

        crosshairTarget =  mc.level.clip(new ClipContext(start, vec3d3, ClipContext.Block.OUTLINE, false ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, entity2));


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
            e = crosshairTarget.getLocation().distanceToSqr(start);
        }


        if (!ignoreEntity){
            AABB box = entity2.getBoundingBox().expandTowards(vec3d2.scale(d)).inflate(1.0, 1.0, 1.0);
            EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(entity2, start, vec3d3, box, entity -> !entity.isSpectator() && entity.isPickable(), e);
            if (entityHitResult != null) {
                Vec3 vec3d4 = entityHitResult.getLocation();
                double g = start.distanceToSqr(vec3d4);
                if (bl && g > 9.0) {
                    crosshairTarget = BlockHitResult.miss(vec3d4, Direction.getApproximateNearest(vec3d2.x, vec3d2.y, vec3d2.z), BlockPos.containing(vec3d4));
                } else if (g < e || crosshairTarget == null) {
                    crosshairTarget = entityHitResult;
                }
            }
        }

        if (crosshairTarget == null)return null;
        if (crosshairTarget instanceof BlockHitResult) {
            return (BlockHitResult) crosshairTarget;
        }
        return null;
        //ChatUtils.sendMsg(Text.of("pos:"+ crosshairTarget.getPos()+ " dis: "+target.distanceTo(crosshairTarget.getPos())));

    }
}
