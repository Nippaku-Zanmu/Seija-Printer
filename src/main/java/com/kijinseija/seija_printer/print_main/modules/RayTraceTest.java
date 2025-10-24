package com.kijinseija.seija_printer.print_main.modules;

import com.kijinseija.seija_printer.Addon;
import com.kijinseija.seija_printer.print_main.printer.util.PredictUtility;
import com.kijinseija.seija_printer.print_main.printer.util.SeijaUtil;
import com.kijinseija.seija_printer.settings.impl.DirectionListSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;

import java.util.HashMap;
import java.util.List;

public class RayTraceTest extends Module {

    public RayTraceTest() {
        super(Addon.CATEGORY, "RayTest", "");
        dirmap = new HashMap<>();
        dirmap.put(Direction.DOWN, new Vec3d(-3.838728915128419E-18, -1.0, 1.2240450621459233E-16));
        dirmap.put(Direction.UP, new Vec3d(0.0, 1.0, 0.0));
        dirmap.put(Direction.EAST, new Vec3d(1.0, -0.0, 1.2246468525851679E-16));
        dirmap.put(Direction.NORTH, new Vec3d(1.2246468525851679E-16, -0.0, -1.0));
        dirmap.put(Direction.SOUTH, new Vec3d(0.0, -0.0, 1.0));
        dirmap.put(Direction.WEST, new Vec3d(-1.0, -0.0, 0.0));

    }

    HashMap<Direction, Vec3d> dirmap;
    SettingGroup sgDefault = settings.getDefaultGroup();
    Setting<Double> x = sgDefault.add(new DoubleSetting.Builder().name("x").build());
    Setting<Double> y = sgDefault.add(new DoubleSetting.Builder().name("y").build());
    Setting<Double> z = sgDefault.add(new DoubleSetting.Builder().name("z").build());

    Setting<List<Direction>> dirs = sgDefault.add(new DirectionListSetting.Builder().name("Dir").build());

    protected final Vec3d getRotationVector(float pitch, float yaw) {
        float f = pitch * ((float) Math.PI / 180);
        float g = -yaw * ((float) Math.PI / 180);
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j);
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
        if (mc.world == null) {
            return;
        }

        //double d = mc.interactionManager.getReachDistance();
        double d = Printer.getINSTANCE().dSetPrintingRange.get();
        //121
        HitResult crosshairTarget = null;

        //crosshairTarget = entity2.raycast(d, 1, false);
        Vec3d vec3d = PredictUtility.getPredPlayerVec().offset(Direction.UP,SeijaUtil.getEyeHeight());
        Vec3d targetVec = new Vec3d(x.get(),y.get(),z.get());
        Vec3d vec3d2 = getRotationVector((float) SeijaUtil.getPitch(targetVec), (float) SeijaUtil.getYaw(targetVec));//entity2.getRotationVec(1.0f);
        ChatUtils.sendMsg(Text.of("P:" +mc.getCameraEntity().getPitch()+","+ SeijaUtil.getPitch(targetVec)));
        ChatUtils.sendMsg(Text.of("Y:" +mc.getCameraEntity().getYaw()+","+ SeijaUtil.getYaw(targetVec)));
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
        if (crosshairTarget.getPos() != null)
            ChatUtils.sendMsg(Text.of(crosshairTarget.getPos() + ":::" + crosshairTarget.getType().name()));
    }

}
