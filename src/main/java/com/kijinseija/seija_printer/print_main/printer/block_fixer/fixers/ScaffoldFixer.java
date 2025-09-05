package com.kijinseija.seija_printer.print_main.printer.block_fixer.fixers;

import com.kijinseija.seija_printer.print_main.printer.block_fixer.AbstractFixer;
import com.kijinseija.seija_printer.print_main.printer.util.BlockRotDataGetter;
import com.kijinseija.seija_printer.print_main.printer.util.BlockUtil;
import com.kijinseija.seija_printer.print_main.printer.util.InvUtil;
import com.kijinseija.seija_printer.print_main.printer.util.SeijaUtil;
import com.kijinseija.seija_printer.print_main.printer.util.records.DirData;
import com.kijinseija.seija_printer.print_main.printer.util.records.DirDataI;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
import com.kijinseija.seija_printer.print_main.printer.util.records.PosInfo;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class ScaffoldFixer extends AbstractFixer {


    public ScaffoldFixer() {
        super("ScaffoldFix");
    }

    @Override
    public int fixBlock(BlockPos pos, BlockState needState) {
        for (Direction off : Direction.values()) {
            if (off == Direction.UP) continue;
            BlockPos helperPos = pos.offset(off);
            if (!(mc.world.getBlockState(helperPos).getBlock() instanceof ScaffoldingBlock
            &&mc.world.getBlockState(helperPos).get(ScaffoldingBlock.DISTANCE)<6)) continue;
            List<Direction> dirs = BlockUtil.getSortedDirs(helperPos,true);
            if (off != Direction.DOWN) {

                if (!dirs.contains(Direction.UP)) continue;
                DirData data = new DirData(helperPos, dirs);

                for (Vec3d clickVec : data.clickVecsInte(Direction.UP)) {

                    PlaceData interactData = PlaceData.NULL;
                    if (pri().bSetIllegalRotate.get()) {
                        interactData = PlaceData.newInstance(helperPos, Direction.UP, clickVec, true, BlockRotDataGetter.getRotateDataFromDir(off.getOpposite()));
                    } else if (Direction.fromHorizontalDegrees(SeijaUtil.getYaw(clickVec)) == off.getOpposite())
                        //1.21 Direction.fromRotation
                    {
                        interactData = PlaceData.newInstance(helperPos, Direction.UP, clickVec, true, null);
                    }
                    if (interactData.valid()){
                        if (InvUtil.switchBlock(Blocks.SCAFFOLDING)) {
                            BlockUtil.interactBlock(interactData);
                            PosInfo blackInfo = new PosInfo(pos, off, clickVec, false, System.currentTimeMillis(), Color.BLACK);
                            pri().blackList.add(blackInfo);
                            return SUCCESS;
                        }else return RETURN;
                    }
                }
            } else {
                dirs.remove(Direction.UP);
                DirData data = new DirData(helperPos, dirs);
                for (Direction clickDir : dirs) {
                    for (Vec3d clickVec : data.clickVecsInte(clickDir)) {
                        PlaceData interactData =  PlaceData.newInstance(helperPos, clickDir, clickVec, true, null);
                        if (InvUtil.switchBlock(Blocks.SCAFFOLDING)) {
                            BlockUtil.interactBlock(interactData);
                            PosInfo blackInfo = new PosInfo(pos, off, clickVec, false, System.currentTimeMillis(), Color.BLACK);
                            pri().blackList.add(blackInfo);
                            return SUCCESS;
                        }else return RETURN;
                    }
                }
            }
        }
        return CONTINUE;
    }

    @Override
    public boolean needFix(BlockPos pos, BlockState needState) {
        return mc.world.getBlockState(pos).getBlock()instanceof AirBlock
            &&needState.getBlock()instanceof ScaffoldingBlock;
    }
}
