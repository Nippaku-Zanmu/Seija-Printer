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

public record DirData(BlockPos placePos, List<Direction> dirs) {
    private static Printer pri = Printer.getINSTANCE();

    public List<Vec3d> getClickVec1(Direction offsetDir) {
        return getClickVec1(offsetDir, 0);
    }

    public Vec3d getClickVec(Direction offsetDir) {
        //if (i >= dirs.size() - 1) return null;
        Vec3d centerVec = placePos.toCenterPos();
        //Direction offsetDir = dirs.get(i);
        return centerVec.offset(offsetDir, pri.bSetStrictVec.get()&&pri.bSetStrictVec.isVisible() ? -0.1 : 0.5);
    }

    public List<Vec3d> getClickVecs(final Direction offsetDir) {
        return getClickVecs(offsetDir, 0);
    }


    public List<Vec3d> getClickVec1(Direction offsetDir, int mode) {
        ArrayList<Vec3d> res = new ArrayList<>();
        BlockPos clickPos = placePos.offset(offsetDir);

        Vec3d clickVec =getClickVec(offsetDir);// placePos.toCenterPos().offset(offsetDir, pri.strictVec.get()&&pri.strictVec.isVisible() ? -0.1 : 0.5);
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

        if (pri.bSetStrictVec.get()&&pri.bSetStrictVec.isVisible()) {
            BlockHitResult result = RayTraceUtil.INSTANCE.getStrictVecResult(clickVec, offsetDir, Printer.getINSTANCE().bSetLiquidInt.get(),1.57);
            if (result.getType() == HitResult.Type.MISS) return res;
            clickVec = result.getPos();
            if (!RayTraceUtil.INSTANCE.rayTrace(clickVec))
                return res;
            if (!(clickVec.x<=clickPos.getX()+1&&clickVec.x>=clickPos.getX()
                &&clickVec.y<=clickPos.getY()+1&&clickVec.y>=clickPos.getY()
                &&clickVec.z<=clickPos.getZ()+1&&clickVec.z>=clickPos.getZ()
            ))return res;
        }
        res.add(clickVec);
        return res;
    }

    //mode 1 上半 2 下半
    public List<Vec3d> getClickVecs(final Direction offsetDir, int mode) {
        BlockPos clickPos = placePos.offset(offsetDir);
//        if (i >= dirs.size() - 1) return new ArrayList<>();
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
//        BlockUtil.getExtendVec(offsetDir, true)
//            .forEach(vec3d -> vecList.add(clickVec.add(vec3d.multiply(0.4))));
        //获取衍生的Vec偏移量,与基础中心Vec相加,放入列表
        if (pri.bSetStrictVec.get()&&pri.bSetStrictVec.isVisible())
            vecList = vecList.stream().map(vec3d -> {
                    BlockHitResult strictVecResult = RayTraceUtil.INSTANCE.getStrictVecResult(vec3d, offsetDir, Printer.getINSTANCE().bSetLiquidInt.get(),1.57);
                    if (strictVecResult.getType() == HitResult.Type.MISS) {
                        return null;
                    }
                    if (!RayTraceUtil.INSTANCE.rayTrace(strictVecResult.getPos()))
                        return null;
                    return strictVecResult.getPos();
                })
                .filter(Objects::nonNull)
                .filter(vec->vec.x<=clickPos.getX()+1.001&&vec.x>=clickPos.getX()-0.001
                    &&vec.y<=clickPos.getY()+1.001&&vec.y>=clickPos.getY()-0.001
                    &&vec.z<=clickPos.getZ()+1.001&&vec.z>=clickPos.getZ()-0.001

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

    public List<Vec3d> clickVecs(Direction offset, int mode) {
        if (pri.bSetMultiVec.get())
            return getClickVecs(offset, mode);
        return getClickVec1(offset, mode);
    }

    public List<Vec3d> clickVecs(Direction offset) {
        return clickVecs(offset, 0);
    }
}
