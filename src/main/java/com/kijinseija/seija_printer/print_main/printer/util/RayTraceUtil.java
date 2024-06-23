package com.kijinseija.seija_printer.print_main.printer.util;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;

import java.util.HashMap;

public class RayTraceUtil {
    private static final Printer pri = Printer.getINSTANCE();
    public static RayTraceUtil INSTANCE = new RayTraceUtil();

    private RayTraceUtil() {
        dirmap = new HashMap<>();
        dirmap.put(Direction.DOWN,new Vec3d(-3.838728915128419E-18, -1.0, 1.2240450621459233E-16));
        dirmap.put(Direction.UP,new Vec3d(0.0, 1.0, 0.0));
        dirmap.put(Direction.EAST,new Vec3d(1.0, -0.0, 1.2246468525851679E-16));
        dirmap.put(Direction.NORTH,new Vec3d(1.2246468525851679E-16, -0.0, -1.0));
        dirmap.put(Direction.SOUTH,new Vec3d(0.0, -0.0, 1.0));
        dirmap.put(Direction.WEST,new Vec3d(-1.0, -0.0, 0.0));

    }
    private final HashMap<Direction,Vec3d> dirmap;
    private final static MinecraftClient mc = MinecraftClient.getInstance();
    /**
     * get strict vec
     *
     * @param vec3d vec 初始粗略的点击点(可能不在支持方块的表面)
     * @param rayDir rayDir 射线计算方向
     * @param includeFluids includeFluids 计算流体
     * @return {@link BlockHitResult}
     * @see BlockHitResult
     */
    public BlockHitResult getStrictVecResult(Vec3d vec3d, Direction rayDir, boolean includeFluids,double rayLength){
        Vec3d vec3d2 = dirmap.get(rayDir);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * rayLength, vec3d2.y * rayLength, vec3d2.z * rayLength);
        return mc.world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, mc.player));
    }

    public static  Vec3d getRotationVector(float pitch, float yaw) {
        float f = pitch * ((float) Math.PI / 180);
        float g = -yaw * ((float) Math.PI / 180);
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j);
    }
    public boolean rayTrace(Vec3d target){
        if (!(pri.bSetRayTrace.isVisible()&&pri.bSetRayTrace.get()))return true;

        Entity entity2 = mc.getCameraEntity();
        if (entity2 == null) {
            return false;
        }
        if (mc.world == null) {
            return false;
        }

        double d = pri.dSetPrintingRange.get();
        HitResult crosshairTarget = null;

        //crosshairTarget = entity2.raycast(d, 1, false);
        Vec3d vec3d = PredictUtility.getPredPlayerVec().offset(Direction.UP,SeijaUtil.getEyeHeight());

        Vec3d vec3d2 = getRotationVector((float) SeijaUtil.getPitch(target), (float) SeijaUtil.getYaw(target));//entity2.getRotationVec(1.0f);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);

        crosshairTarget =  mc.world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, false ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, entity2));


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
            e = crosshairTarget.getPos().squaredDistanceTo(vec3d);
        }


        if (!pri.bSetIgnoreEntity.get()){
            Box box = entity2.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0, 1.0, 1.0);
            EntityHitResult entityHitResult = ProjectileUtil.raycast(entity2, vec3d, vec3d3, box, entity -> !entity.isSpectator() && entity.canHit(), e);
            if (entityHitResult != null) {
                Vec3d vec3d4 = entityHitResult.getPos();
                double g = vec3d.squaredDistanceTo(vec3d4);
                if (bl && g > 9.0) {
                    crosshairTarget = BlockHitResult.createMissed(vec3d4, Direction.getFacing(vec3d2.x, vec3d2.y, vec3d2.z), BlockPos.ofFloored(vec3d4));
                } else if (g < e || crosshairTarget == null) {
                    crosshairTarget = entityHitResult;
                }
            }
        }

        if (crosshairTarget == null)return false;
        if (crosshairTarget.getType()!= HitResult.Type.BLOCK)return false;
        //ChatUtils.sendMsg(Text.of("pos:"+ crosshairTarget.getPos()+ " dis: "+target.distanceTo(crosshairTarget.getPos())));
        return target.distanceTo(crosshairTarget.getPos())<=0.1;
    }
}
