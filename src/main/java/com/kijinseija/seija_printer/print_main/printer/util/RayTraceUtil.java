package com.kijinseija.seija_printer.print_main.printer.util;

import com.kijinseija.seija_printer.Addon;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.HashMap;

public class RayTraceUtil {
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
    public BlockHitResult getStrictVecResult(Vec3d vec3d, Direction rayDir, boolean includeFluids){
        Vec3d vec3d2 = dirmap.get(rayDir);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * 1, vec3d2.y * 1, vec3d2.z * 1);
        return mc.world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, mc.player));
    }
}
