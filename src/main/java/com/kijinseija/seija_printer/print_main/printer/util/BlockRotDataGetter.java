package com.kijinseija.seija_printer.print_main.printer.util;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import com.kijinseija.seija_printer.print_main.printer.util.records.RotationData;
import net.minecraft.block.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;

public class BlockRotDataGetter {
    private static final Printer pri = Printer.getINSTANCE();

    /**
     * get vec
     * 获取强制放置指定方块状态的非法转头方向
     *
     * @param bs bs 需要的State
     * @return {@link RotationData}
     * @see RotationData
     */
    @Nullable
    public static RotationData getRotData(BlockState bs) {
        if (!pri.illegalRotate.get()) return null;//没开非法转头就结束
        Block block = bs.getBlock();
        //H rotate YClock
        //Anvil
        if (block instanceof AnvilBlock) {
            return getVecFromDir(bs.get(Properties.HORIZONTAL_FACING).rotateYCounterclockwise());
        }
        //H opposite
        //Trapdoor Chest EnderChestBlock AbstractFurnaceBlock LecternBlock StonecutterBlock BeehiveBlock
        //RedStoneGate DripLeaf CarVedPumpkin ChiseledBookshelf EndProtalFra FlowerbedBlock
        //GlazedTerracottaBlock JigsawBlock LoomBlock
        if (block instanceof TrapdoorBlock || block instanceof ChestBlock || block instanceof EnderChestBlock
            || block instanceof AbstractFurnaceBlock || block instanceof LecternBlock || block instanceof StonecutterBlock
            || block instanceof BeehiveBlock || block instanceof AbstractRedstoneGateBlock
            || block instanceof SmallDripleafBlock || block instanceof BigDripleafBlock
            || block instanceof CarvedPumpkinBlock || block instanceof ChiseledBookshelfBlock
            || block instanceof EndPortalFrameBlock || block instanceof FlowerbedBlock
            || block instanceof GlazedTerracottaBlock || block instanceof JigsawBlock
            || block instanceof LoomBlock
        ) {
            return getVecFromDir(bs.get(Properties.HORIZONTAL_FACING).getOpposite());
        }

        //H nor
        //Stair FenceGateBlock CalibratedSculkSensorBlock CampfireBlock Door Bed

        //rotate +=180
        //Sign HSign Banner
        if (block instanceof SignBlock
            || block instanceof HangingSignBlock || block instanceof BannerBlock) {
            return getVecFromRotProp(bs.get(Properties.ROTATION));
        }

        //rotate

        //Fac Oppo
        //PistonBlock DispenserBlock DropperBlock BarrelBlock CommandBlock
        if (block instanceof PistonBlock || block instanceof DispenserBlock
            || block instanceof DropperBlock || block instanceof BarrelBlock
            || block instanceof CommandBlock) {
            return getVecFromDir(bs.get(Properties.FACING).getOpposite());
        }

        //Fac
        //observer


        if (bs.getProperties().contains(Properties.FACING)) {
            return getVecFromDir(bs.get(Properties.FACING));
        }
        if (bs.getProperties().contains(Properties.HORIZONTAL_FACING)) {
            return getVecFromDir(bs.get(Properties.HORIZONTAL_FACING));
        }
        if (bs.getProperties().contains(Properties.ROTATION)) {
            return getVecFromRotProp2(bs.get(Properties.ROTATION));
        }

        return null;
    }

    public static RotationData getVecFromDir(Direction dir) {
        RotationData data = new RotationData(0, 0);
        switch (dir) {
            case EAST -> data = new RotationData(-90, 0);
            case WEST -> data = new RotationData(90, 0);

            case NORTH -> data = new RotationData(180, 0);

            case SOUTH -> data = new RotationData(0, 0);

            case DOWN -> data = new RotationData(0, 90);

            case UP -> data = new RotationData(0, -90);

        }
        return data;
    }

    public static RotationData getVecFromRotProp(int prop) {
        return new RotationData(MathHelper.wrapDegrees(22.5 * prop - 180), 0);
    }

    public static RotationData getVecFromRotProp2(int prop) {
        return new RotationData(MathHelper.wrapDegrees(22.5 * prop), 0);
    }
}
