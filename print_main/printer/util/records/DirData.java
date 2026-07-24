/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.util.records;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import com.kijinseija.seija_printer.print_main.printer.util.BlockUtil;
import com.kijinseija.seija_printer.print_main.printer.util.RayTraceUtil;
import java.util.*;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public record DirData(BlockPos placePos, List<Direction> dirs) {
    private static Printer pri = Printer.getINSTANCE();

    public List<Vec3> getClickVec1(Direction offsetDir, boolean randOffset, boolean strictVec, boolean raytrace, boolean ignoreEntity, double rayRange) {
        return getClickVec1(offsetDir, 0, randOffset, strictVec, raytrace, ignoreEntity, rayRange);
    }


    public Vec3 getClickVec(Direction offsetDir, boolean strictVec) {
        //if (i >= dirs.size() - 1) return null;
        Vec3 centerVec = placePos.getCenter();
        //Direction offsetDir = dirs.get(i);
        return centerVec.relative(offsetDir, strictVec ? -0.1 : 0.5);
    }

    public List<Vec3> getClickVecs(final Direction offsetDir, boolean randOffset, boolean strictVec, boolean raytrace, boolean ignoreEntity, double rayRange) {
        return getClickVecs(offsetDir, 0, randOffset, strictVec, raytrace, ignoreEntity, rayRange);
    }


    public List<Vec3> getClickVec1(Direction offsetDir, int mode, boolean randOffset, boolean strictVec, boolean raytrace, boolean ignoreEntity, double rayRange) {
        ArrayList<Vec3> res = new ArrayList<>();
        BlockPos clickPos = placePos.relative(offsetDir);

        Vec3 clickVec = getClickVec(offsetDir, strictVec);// placePos.toCenterPos().offset(offsetDir, pri.strictVec.get()&&pri.strictVec.isVisible() ? -0.1 : 0.5);
        if (randOffset)
            clickVec = BlockUtil.randomOffsetVec(clickVec, offsetDir);
        if (offsetDir.getAxis() != Direction.Axis.Y)
            switch (mode) {
                case 1:
                    clickVec = clickVec.relative(Direction.UP, 0.2);
                    break;
                case 2:
                    clickVec = clickVec.relative(Direction.DOWN, 0.2);

            }

        if (strictVec) {
            BlockHitResult result = RayTraceUtil.INSTANCE.getStrictVecResult(clickVec, offsetDir, Printer.getINSTANCE().bSetLiquidInt.get(), 1.57);
            if (result.getType() == HitResult.Type.MISS) return res;
            clickVec = result.getLocation();
            if (!RayTraceUtil.INSTANCE.rayTrace(placePos.relative(offsetDir), offsetDir.getOpposite(), clickVec, raytrace, ignoreEntity, rayRange))
                return res;
            if (!(clickVec.x <= clickPos.getX() + 1 && clickVec.x >= clickPos.getX()
                && clickVec.y <= clickPos.getY() + 1 && clickVec.y >= clickPos.getY()
                && clickVec.z <= clickPos.getZ() + 1 && clickVec.z >= clickPos.getZ()
            )) return res;
        }
        res.add(clickVec);
        return res;
    }

    //mode 1 上半 2 下半
    public List<Vec3> getClickVecs(final Direction offsetDir, int mode, boolean randOffset, boolean strictVec,
                                    boolean raytrace, boolean ignoreEntity, double rayRange) {
        BlockPos clickPos = placePos.relative(offsetDir);
//        if (i >= dirs.size() - 1) return new ArrayList<>();
        List<Vec3> vecList = new LinkedList<>();
        //装可用的Vec
        //final Direction offsetDir = dirs.get(i);//偏移方向

        Vec3 clickVec = getClickVec(offsetDir, strictVec);//基础的中心Vec
        if (randOffset) {//随机offset 用于bypass
            vecList.add(BlockUtil.randomOffsetVec(clickVec, offsetDir));
        } else
            vecList.add(clickVec);
        for (Vec3 extendVec : BlockUtil.getExtendVec(offsetDir, true)) {
            vecList.add(clickVec.add(extendVec.scale(0.4)));
        }
//        BlockUtil.getExtendVec(offsetDir, true)
//            .forEach(vec3d -> vecList.add(clickVec.add(vec3d.multiply(0.4))));
        //获取衍生的Vec偏移量,与基础中心Vec相加,放入列表
        if (strictVec)
            vecList = vecList.stream().map(vec3d -> {
                    BlockHitResult strictVecResult = RayTraceUtil.INSTANCE.getStrictVecResult(vec3d, offsetDir, Printer.getINSTANCE().bSetLiquidInt.get(), 1.57);
                    if (strictVecResult.getType() == HitResult.Type.MISS) {
                        return null;
                    }
                    if (!RayTraceUtil.INSTANCE.rayTrace(placePos.relative(offsetDir), offsetDir.getOpposite(), strictVecResult.getLocation(), raytrace, ignoreEntity, rayRange))
                        return null;
                    return strictVecResult.getLocation();
                })
                .filter(Objects::nonNull)
                .filter(vec -> vec.x <= clickPos.getX() + 1.001 && vec.x >= clickPos.getX() - 0.001
                    && vec.y <= clickPos.getY() + 1.001 && vec.y >= clickPos.getY() - 0.001
                    && vec.z <= clickPos.getZ() + 1.001 && vec.z >= clickPos.getZ() - 0.001

                )
                //判断是否在要放置的方块里面
                .collect(Collectors.toList());
        if (offsetDir.getAxis() != Direction.Axis.Y)
            switch (mode) {
                case 1 -> vecList.removeIf(vec3d -> vec3d.y - Math.floor(vec3d.y) <= 0.5);
                case 2 -> vecList.removeIf(vec3d -> vec3d.y - Math.floor(vec3d.y) >= 0.5);
            }
        return vecList;

    }

    public List<Vec3> clickVecs(Direction offset, int mode, DirDataConfig cfg) {
        if (cfg.multiVec)
            return getClickVecs(offset, mode, cfg.randOffset, cfg.strictVec, cfg.raytrace, cfg.ignoreEntity, cfg.rayRange);
        return getClickVec1(offset, mode, cfg.randOffset, cfg.strictVec, cfg.raytrace, cfg.ignoreEntity, cfg.rayRange);
    }

    public List<Vec3> clickVecs(Direction offset, DirDataConfig cfg) {
        return clickVecs(offset, 0, cfg);
    }

    public List<Vec3> clickVecs(Direction offset) {
        return clickVecs(offset, 0, DirDataConfig.getDefault());
    }

    public List<Vec3> clickVecs(Direction offset, int mode) {
        return clickVecs(offset, mode, DirDataConfig.getDefault());
    }

    public List<Vec3> getClickVecs(Direction offsetDir) {
        return getClickVecs(offsetDir, pri.bSetRandomOffset.get(), pri.bSetStrictVec.get() && pri.bSetStrictVec.isVisible(), pri.bSetRayTrace.isVisible() && pri.bSetRayTrace.get(), pri.bSetIgnoreEntity.get(), pri.dSetPrintingRange.get());
    }

    public static class DirDataConfig {
        private static final DirDataConfig DEFAULT = new DirDataConfig();

        public static final DirDataConfig getDefault() {
            DEFAULT.multiVec = pri.bSetMultiVec.get();
            DEFAULT.randOffset = pri.bSetRandomOffset.get();
            DEFAULT.strictVec = pri.bSetStrictVec.get() && pri.bSetStrictVec.isVisible();
            DEFAULT.raytrace = pri.bSetRayTrace.isVisible() && pri.bSetRayTrace.get();
            DEFAULT.ignoreEntity = pri.bSetIgnoreEntity.get();
            DEFAULT.rayRange = pri.dSetPrintingRange.get();
            return DEFAULT;
        }

        public boolean multiVec = pri.bSetMultiVec.get();
        public boolean randOffset = pri.bSetRandomOffset.get();
        public boolean strictVec = pri.bSetStrictVec.get() && pri.bSetStrictVec.isVisible();
        public boolean raytrace = pri.bSetRayTrace.isVisible() && pri.bSetRayTrace.get();
        public boolean ignoreEntity = pri.bSetIgnoreEntity.get();
        public double rayRange = pri.dSetPrintingRange.get();

        public DirDataConfig setMultiVec(boolean multiVec) {
            this.multiVec = multiVec;
            return this;
        }

        public DirDataConfig setRandOffset(boolean randOffset) {
            this.randOffset = randOffset;
            return this;
        }

        public DirDataConfig setStrictVec(boolean strictVec) {
            this.strictVec = strictVec;
            return this;
        }

        public DirDataConfig setRaytrace(boolean raytrace) {
            this.raytrace = raytrace;
            return this;
        }

        public DirDataConfig setIgnoreEntity(boolean ignoreEntity) {
            this.ignoreEntity = ignoreEntity;
            return this;
        }

        public DirDataConfig setRayRange(double rayRange) {
            this.rayRange = rayRange;
            return this;
        }
    }



    public List<Vec3> getClickVecInte1(Direction offsetDir) {
        return getClickVecInte1(offsetDir, 0);
    }

    public Vec3 getClickVecInte(Direction offsetDir) {
        //if (i >= dirs.size() - 1) return null;
        Vec3 centerVec = placePos.getCenter();
        //Direction offsetDir = dirs.get(i);
        return centerVec.relative(offsetDir, pri.bSetStrictVec.get()&&pri.isStrictVecInte()?0.501:0.5);
    }

    public List<Vec3> getClickVecsInte(final Direction offsetDir) {
        return getClickVecsInte(offsetDir, 0);
    }


    public List<Vec3> getClickVecInte1(Direction offsetDir, int mode) {
        ArrayList<Vec3> res = new ArrayList<>();

        Vec3 clickVec = placePos.getCenter().relative(offsetDir, pri.bSetStrictVec.get()&&pri.isStrictVecInte()?0.501:0.5);
        if (pri.bSetRandomOffset.get())
            clickVec = BlockUtil.randomOffsetVec(clickVec, offsetDir);
        if (offsetDir.getAxis() != Direction.Axis.Y)
            clickVec = switch (mode) {
                case 1 -> clickVec.relative(Direction.UP, 0.2);
                case 2 -> clickVec.relative(Direction.DOWN, 0.2);
                default -> clickVec;
            };

        if (pri.bSetStrictVec.get()&&pri.isStrictVecInte()) {
            BlockHitResult result = RayTraceUtil.INSTANCE.getStrictVecResult(clickVec, offsetDir.getOpposite(), Printer.getINSTANCE().bSetLiquidInt.get(),1);
            if (result.getType() == HitResult.Type.MISS) return res;
            clickVec = result.getLocation();
            if (!RayTraceUtil.INSTANCE.rayTrace(placePos,offsetDir,clickVec))
                return res;
        }
        res.add(clickVec);
        return res;
    }

    //mode 1 上半 2 下半
    public List<Vec3> getClickVecsInte(final Direction offsetDir, int mode) {
        List<Vec3> vecList = new LinkedList<>();
        //装可用的Vec
        //final Direction offsetDir = dirs.get(i);//偏移方向

        Vec3 clickVec = getClickVecInte(offsetDir);//基础的中心Vec
        if (pri.bSetRandomOffset.get()) {//随机offset 用于bypass
            vecList.add(BlockUtil.randomOffsetVec(clickVec, offsetDir));
        } else
            vecList.add(clickVec);

        for (Vec3 extendVec : BlockUtil.getExtendVec(offsetDir, true)) {
            vecList.add(clickVec.add(extendVec.scale(0.4)));
        }
        //.forEach(vec3d -> vecList.add(clickVec.add(vec3d.multiply(0.4))));
        //获取衍生的Vec偏移量,与基础中心Vec相加,放入列表
        if (pri.bSetStrictVec.get()&&pri.isStrictVecInte())
            vecList = vecList.stream().map(vec3d -> {
                    BlockHitResult strictVecResult = RayTraceUtil.INSTANCE.getStrictVecResult(vec3d, offsetDir.getOpposite(), Printer.getINSTANCE().bSetLiquidInt.get(),1);
                    if (strictVecResult.getType() == HitResult.Type.MISS) {
                        return null;
                    }
                    if (!RayTraceUtil.INSTANCE.rayTrace(placePos,offsetDir,strictVecResult.getLocation()))
                        return null;
                    return strictVecResult.getLocation();
                })
                .filter(Objects::nonNull).collect(Collectors.toList());
        if (offsetDir.getAxis() != Direction.Axis.Y)
            switch (mode) {
                case 1:
                    vecList.removeIf(vec3d -> vec3d.y - Math.floor(vec3d.y) <= 0.5);
                    break;
                case 2:
                    vecList.removeIf(vec3d -> vec3d.y - Math.floor(vec3d.y) >= 0.5);
            }
        return vecList;

    }

    public List<Vec3> clickVecsInte(Direction offset) {
        return clickVecsInte(offset,0);
    }

    public List<Vec3> clickVecsInte(Direction offset, int mode) {
        if (pri.bSetMultiVec.get())
            return getClickVecsInte(offset,mode);
        return getClickVecInte1(offset, mode);
    }
}
