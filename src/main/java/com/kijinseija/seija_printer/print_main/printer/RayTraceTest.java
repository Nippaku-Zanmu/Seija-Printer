package com.kijinseija.seija_printer.print_main.printer;

import com.kijinseija.seija_printer.Addon;
import com.kijinseija.seija_printer.print_main.settings.DirectionListSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.HashMap;
import java.util.List;

public class RayTraceTest extends Module {

    public RayTraceTest() {
        super(Addon.CATEGORY, "RayTest", "");
        dirmap = new HashMap<>();
        dirmap.put(Direction.DOWN,new Vec3d(-3.838728915128419E-18, -1.0, 1.2240450621459233E-16));
        dirmap.put(Direction.UP,new Vec3d(0.0, 1.0, 0.0));
        dirmap.put(Direction.EAST,new Vec3d(1.0, -0.0, 1.2246468525851679E-16));
        dirmap.put(Direction.NORTH,new Vec3d(1.2246468525851679E-16, -0.0, -1.0));
        dirmap.put(Direction.SOUTH,new Vec3d(0.0, -0.0, 1.0));
        dirmap.put(Direction.WEST,new Vec3d(-1.0, -0.0, 0.0));

    }
    HashMap<Direction,Vec3d> dirmap;
    SettingGroup sgDefault = settings.getDefaultGroup();
    Setting<Double> x = sgDefault.add(new DoubleSetting.Builder().name("x").build());
    Setting<Double> y = sgDefault.add(new DoubleSetting.Builder().name("x").build());
    Setting<Double> z = sgDefault.add(new DoubleSetting.Builder().name("x").build());

    Setting<List<Direction>>  dirs = sgDefault.add(new DirectionListSetting.Builder().name("Dir").build());
    @Override
    public void onActivate() {
//        mc.crosshairTarget
//        Vec3d vec3d = mc.player.getCameraPosVec(1);
//        Vec3d vec3d2 = mc.player.getRotationVec(1);
        if (dirs.get().size()==0)return;
        Vec3d vec3d = new Vec3d(x.get(),y.get(),z.get());
        Vec3d vec3d2 = dirmap.get(dirs.get().get(0));
        Vec3d vec3d3 = vec3d.add(vec3d2.x * 1, vec3d2.y * 1, vec3d2.z * 1);
        BlockHitResult raycast = mc.world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, false ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, mc.player));

        ChatUtils.sendMsg(Text.of(raycast.getPos() + ":::"+raycast.getType().name() ));

    }

}
