package com.kijinseija.seija_printer.printer.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public record DirData(List<Direction> dirs, BlockPos placePos) {

    public Vec3d getClickVec(int i){
        if (i>=dirs.size()-1)return null;
        List<Vec3d> vecList = new ArrayList<>();
        Vec3d centerVec = placePos.toCenterPos();
        return centerVec.offset(dirs.get(i),0.5);
    }
    public List<Vec3d> getClickVecs(int i){
        if (i>=dirs.size()-1)return null;
        List<Vec3d> vecList = new LinkedList<>();
        Vec3d clickVec = getClickVec(i);
        vecList.add(clickVec);
        BlockUtil.getExtendVec(dirs.get(i),true)
            .forEach(vec3d -> vecList.add(clickVec.add(vec3d.multiply(0.3))));
        return vecList;
    }


}
