package com.kijinseija.seija_printer.print_main.printer.util.records;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import com.kijinseija.seija_printer.print_main.printer.util.BlockUtil;
import com.kijinseija.seija_printer.print_main.printer.util.RayTraceUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public record DirDataI(BlockPos placePos, List<Direction> dirs) {
    private static Printer pri = Printer.getINSTANCE();

    //    @Nullable
//    public Vec3d getClickVec(Direction dir, boolean strictVec, boolean randomOffset) {
//        if (i >= dirs.size() - 1) return null;
//        Vec3d centerVec = placePos.toCenterPos();
//        Direction offsetDir = dirs.get(i);
//        if (strictVec) {
//            BlockHitResult result = RayTraceUtil.INSTANCE.getStrictVecResult(centerVec.offset(offsetDir, 0.5), offsetDir, Printer.getINSTANCE().liquidInt.get());
//            if (result.getType() == HitResult.Type.MISS) return null;
//            return result.getPos();
//        }
//        return centerVec.offset(offsetDir, 0.5);
//    }
    public List<Vec3d> getClickVec1(Direction offsetDir) {
        return getClickVec1(offsetDir, 0);
//        ArrayList<Vec3d> res = new ArrayList<>();
////        if (i >= dirs.size() - 1) return null;
////        Direction offsetDir = dirs.get(i);
//
//        Vec3d clickVec = placePos.toCenterPos().offset(offsetDir,0.5);
//        if (pri.randomOffset.get())
//            clickVec = BlockUtil.randomOffsetVec(clickVec,offsetDir);
//
//        if (pri.strictVec.get()) {
//            BlockHitResult result = RayTraceUtil.INSTANCE.getStrictVecResult(clickVec, offsetDir, Printer.getINSTANCE().liquidInt.get());
//            if (result.getType() == HitResult.Type.MISS) return res;
//            clickVec = result.getPos();
//        }
//        res.add(clickVec);
//        return res;
    }

    public Vec3d getClickVec(Direction offsetDir) {
        //if (i >= dirs.size() - 1) return null;
        Vec3d centerVec = placePos.toCenterPos();
        //Direction offsetDir = dirs.get(i);
        return centerVec.offset(offsetDir, pri.bSetStrictVec.get()&&pri.isStrictVecInte()?0.501:0.5);
    }

    public List<Vec3d> getClickVecs(final Direction offsetDir) {
        return getClickVecs(offsetDir, 0);
    }


    public List<Vec3d> getClickVec1(Direction offsetDir, int mode) {
        ArrayList<Vec3d> res = new ArrayList<>();

        Vec3d clickVec = placePos.toCenterPos().offset(offsetDir, pri.bSetStrictVec.get()&&pri.isStrictVecInte()?0.501:0.5);
        if (pri.bSetRandomOffset.get())
            clickVec = BlockUtil.randomOffsetVec(clickVec, offsetDir);
        if (offsetDir.getAxis() != Direction.Axis.Y)
            switch (mode) {
                case 1:
                    clickVec = clickVec.offset(Direction.UP, 0.2);
                    break;
                case 2:
                    clickVec = clickVec.offset(Direction.DOWN, 0.2);

            }

        if (pri.bSetStrictVec.get()&&pri.isStrictVecInte()) {
            BlockHitResult result = RayTraceUtil.INSTANCE.getStrictVecResult(clickVec, offsetDir.getOpposite(), Printer.getINSTANCE().bSetLiquidInt.get(),1);
            if (result.getType() == HitResult.Type.MISS) return res;
            clickVec = result.getPos();
            if (!RayTraceUtil.INSTANCE.rayTrace(clickVec))
                return res;
        }
        res.add(clickVec);
        return res;
    }

    //mode 1 上半 2 下半
    public List<Vec3d> getClickVecs(final Direction offsetDir, int mode) {
        List<Vec3d> vecList = new LinkedList<>();
        //装可用的Vec
        //final Direction offsetDir = dirs.get(i);//偏移方向

        Vec3d clickVec = getClickVec(offsetDir);//基础的中心Vec
        if (pri.bSetRandomOffset.get()) {//随机offset 用于bypass
            vecList.add(BlockUtil.randomOffsetVec(clickVec, offsetDir));
        } else
            vecList.add(clickVec);

        for (Vec3d extendVec : BlockUtil.getExtendVec(offsetDir, true)) {
            vecList.add(clickVec.add(extendVec.multiply(0.4)));
        }
            //.forEach(vec3d -> vecList.add(clickVec.add(vec3d.multiply(0.4))));
        //获取衍生的Vec偏移量,与基础中心Vec相加,放入列表
        if (pri.bSetStrictVec.get()&&pri.isStrictVecInte())
            vecList = vecList.stream().map(vec3d -> {
                    BlockHitResult strictVecResult = RayTraceUtil.INSTANCE.getStrictVecResult(vec3d, offsetDir.getOpposite(), Printer.getINSTANCE().bSetLiquidInt.get(),1);
                    if (strictVecResult.getType() == HitResult.Type.MISS) {
                        return null;
                    }
                    if (!RayTraceUtil.INSTANCE.rayTrace(strictVecResult.getPos()))
                        return null;
                    return strictVecResult.getPos();
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

    public List<Vec3d> clickVecs(Direction offset) {
        return clickVecs(offset,0);
    }

    public List<Vec3d> clickVecs(Direction offset, int mode) {
        if (pri.bSetMultiVec.get())
            return getClickVecs(offset,mode);
        return getClickVec1(offset, mode);
    }
}
