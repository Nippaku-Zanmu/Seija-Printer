/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.util;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import com.kijinseija.seija_printer.print_main.printer.task_manager.RotationManager;
import com.kijinseija.seija_printer.print_main.printer.task_manager.RotationTask;
import com.kijinseija.seija_printer.print_main.printer.task_manager.Task;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceDataPack;
import com.kijinseija.seija_printer.print_main.printer.util.records.PosInfo;
import com.kijinseija.seija_printer.print_main.printer.util.records.RotationData;
import fi.dy.masa.litematica.data.DataManager;
import com.kijinseija.seija_printer.utils.player.Rotations;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.BeaconBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.CartographyTableBlock;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.DragonEggBlock;
import net.minecraft.world.level.block.DropperBlock;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.GrindstoneBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.LoomBlock;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.SmithingTableBlock;
import net.minecraft.world.level.block.StonecutterBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import java.util.*;
import java.util.stream.Collectors;

public class BlockUtil {
    private static Printer pri = Printer.getINSTANCE();
    private static Minecraft mc = Minecraft.getInstance();



    public static List<Task> genTasks(PlaceDataPack pdp) {
        boolean isPlaceMode = pdp.placeMode();
        boolean isBucket = mc.player.getMainHandItem().getItem() instanceof BucketItem;
        PlaceData data = pdp.data();
        Vec3 hitVec = data.hitVec();
        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(data.pos().getX(), data.pos().getY(), data.pos().getZ());
        Direction dir = data.dir();
        ArrayList<Task> tasks = new ArrayList<>();


        Runnable r = () -> {
            boolean sneakToggle = false;
            if (isPlaceMode && pri.bSetSneak.get()) {
                if (!mc.player.isShiftKeyDown()) {
                    sneakToggle = true;
                    mc.player.setShiftKeyDown(true);
                }
            }

            if (data.test() == null || data.test().getAsBoolean()) {
                illegalRotate(data.exRotateData());
                if (isBucket) {
                    mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
                }

                if (pri.bSetPacketPlace.get()) {
                    mc.player.connection.send(new ServerboundUseItemOnPacket(InteractionHand.MAIN_HAND, getHitRes(pos, dir, hitVec), SeijaUtil.getSequence()));
                    mc.player.connection.send(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
                } else {
                    mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, getHitRes(pos, dir, hitVec));
                    mc.player.swing(InteractionHand.MAIN_HAND);
                }

                PosInfo blackInfo = isPlaceMode ? RenderHelper.getBlackInfo(pos.relative(dir), dir, hitVec, true):
                    RenderHelper.getBlackInfo(pos, dir, hitVec, false);
                pri.blackList.add(blackInfo);
                RenderUtil.renderList.add(blackInfo);
            }

            if (sneakToggle) {
                mc.player.setShiftKeyDown(false);
            }

        };
        if (pri.bSetRotate.get() || isBucket) {
            tasks.add(new RotationTask(null,RotationData.fromVec(hitVec)));
        }
        if (data.exRotateData()!=null)
            tasks.add(new RotationTask(null,data.exRotateData()));

        tasks.add(new Task(r));
        return tasks;
    }

    public static void applyPlaceData(PlaceDataPack pdp) {

        RotationManager.INSTANCE.addTask(genTasks(pdp));

        if (true)return;
        boolean isPlaceMode = pdp.placeMode();
        boolean isBucket = mc.player.getMainHandItem().getItem() instanceof BucketItem;
        PlaceData data = pdp.data();
        Vec3 hitVec = data.hitVec();
        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(data.pos().getX(), data.pos().getY(), data.pos().getZ());
        Direction dir = data.dir();


        Runnable r = () -> {
            boolean sneakToggle = false;
            if (isPlaceMode && pri.bSetSneak.get()) {
                if (!mc.player.isShiftKeyDown()) {
                    sneakToggle = true;
                    mc.player.setShiftKeyDown(true);
                }
            }

            if (data.test() == null || data.test().getAsBoolean()) {
                illegalRotate(data.exRotateData());
                if (isBucket) {
                    mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
                }

                if (pri.bSetPacketPlace.get()) {
                    mc.player.connection.send(new ServerboundUseItemOnPacket(InteractionHand.MAIN_HAND, getHitRes(pos, dir, hitVec), SeijaUtil.getSequence()));
                    mc.player.connection.send(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
                } else {
                    mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, getHitRes(pos, dir, hitVec));
                    mc.player.swing(InteractionHand.MAIN_HAND);
                }

                PosInfo blackInfo = isPlaceMode ? RenderHelper.getBlackInfo(pos.relative(dir), dir, hitVec, true):
                RenderHelper.getBlackInfo(pos, dir, hitVec, false);
                pri.blackList.add(blackInfo);
                RenderUtil.renderList.add(blackInfo);
            }

            if (sneakToggle) {
                mc.player.setShiftKeyDown(false);
            }

        };
        if (pri.bSetRotate.get() || isBucket) {
            if (pri.bSetPacketRotate.get()) {
                mc.player.connection.send(new ServerboundMovePlayerPacket.Rot((float) SeijaUtil.getYaw(hitVec), (float) SeijaUtil.getPitch(hitVec), mc.player.onGround(), mc.player.horizontalCollision));
                r.run();
            } else
                Rotations.rotate(SeijaUtil.getYaw(hitVec), SeijaUtil.getPitch(hitVec), r);
        } else {
            r.run();
        }
    }

    public static void interactBlock(PlaceData data) {
        applyPlaceData(new PlaceDataPack(data,false));
//        Vec3d hitVec = data.hitVec();
//        final BlockPos.Mutable pos = new BlockPos.Mutable(data.pos().getX(), data.pos().getY(), data.pos().getZ());
//        Direction dir = data.dir();
//        Runnable r = () -> {
//            if (data.test() == null || data.test().getAsBoolean()) {
//                if (pri.bSetIllegalRotate.get() && data.exRotateData() != null) {
//                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround((float) data.exRotateData().yaw(), (float) data.exRotateData().pitch(), mc.player.isOnGround(), mc.player.horizontalCollision));
//                }//非法转头
//                if (pri.bSetPacketPlace.get()) {
//                    mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, getHitRes(pos, dir, hitVec), SeijaUtil.getSequence()));
//                    mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
//                } else {
//                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, getHitRes(pos, dir, hitVec));
//                    mc.player.swingHand(Hand.MAIN_HAND);
//                }
//
//                PosInfo blackInfo = RenderHelper.getBlackInfo(pos, dir, hitVec, false);
//                pri.blackList.add(blackInfo);
//                RenderUtil.renderList.add(blackInfo);
//            }
//        };
//        if (pri.bSetRotate.get()) {
//            if (pri.bSetPacketRotate.get()) {
//                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround((float) SeijaUtil.getYaw(hitVec), (float) SeijaUtil.getPitch(hitVec), mc.player.isOnGround(), mc.player.horizontalCollision));
//                r.run();
//            } else
//                Rotations.rotate(SeijaUtil.getYaw(hitVec), SeijaUtil.getPitch(hitVec), r);
//        } else r.run();
    }

    public static void placeBlock(PlaceData data) {
        applyPlaceData(new PlaceDataPack(data,true));
//        final BlockPos.Mutable pos = new BlockPos.Mutable(data.pos().getX(), data.pos().getY(), data.pos().getZ());
//
//
//        Direction dir = data.dir();
//        Vec3d hitVec = data.hitVec();
//        final boolean isBucket = mc.player.getMainHandStack().getItem() instanceof BucketItem;
//
//        Runnable r = () -> {
//            boolean sneakToggle = false;
//            if (pri.bSetSneak.get()) {
//                if (!mc.player.isSneaking()) {
//                    sneakToggle = true;
//                    mc.player.setSneaking(true);
//                }
//            }
//            if (data.test() == null || data.test().getAsBoolean()) {
//                //非法转头
//                illegalRotate(data.exRotateData());
//                if (isBucket) {
//                    mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
//                }
//                if (pri.bSetPacketPlace.get()) {
//                    mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, getHitRes(pos, dir, hitVec), SeijaUtil.getSequence()));
//                    mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
//                } else {
//                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, getHitRes(pos, dir, hitVec));
//                    mc.player.swingHand(Hand.MAIN_HAND);
//                }
//                PosInfo blackInfo = RenderHelper.getBlackInfo(pos.offset(dir), dir, hitVec, true);
//                pri.blackList.add(blackInfo);
//                RenderUtil.renderList.add(blackInfo);
//            }
//            if (sneakToggle) {
//                mc.player.setSneaking(false);
//            }
//
//        };
//        if (pri.bSetRotate.get() || isBucket) {
//            if (pri.bSetPacketRotate.get()) {
//                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround((float) SeijaUtil.getYaw(hitVec), (float) SeijaUtil.getPitch(hitVec), mc.player.isOnGround(), mc.player.horizontalCollision));
//                r.run();
//            } else
//                Rotations.rotate(SeijaUtil.getYaw(hitVec), SeijaUtil.getPitch(hitVec), r);
//        } else {
//            r.run();
//        }

    }
    //t inte f plac
    public static List<Direction> getSortedDirs(BlockPos pos,boolean mode) {
        List<Direction> dirs =  mode? getInteractDir(pos) : getDirs(pos);
        return pri.bSetSortDir.get() ?DirSorter.sort(dirs,pos,mode):dirs;
//        if (mode){
//            return pri.bSetSortDir.get() ? DirSorter.sort(getInteractDir(pos), pos) : getInteractDir(pos);
//        }
//        return pri.bSetSortDir.get() ? DirSorter.sort(getDirs(pos), pos) : getDirs(pos);
    }
    public static List<Direction> getInteractDir(BlockPos pos) {
        if (!mc.level.getWorldBorder().isWithinBounds(pos))return new LinkedList<>();
        return (pri.bSetStrictDir.get() ? canTorchFac(pos) : Arrays.asList(Direction.values())).stream()
            .filter(dir -> (!pri.bSetStrictDir.get()) || canPlaceIn(pos.relative(dir)))
            .filter(dir -> pri.dRangeSetPrintingYRange.get().isInRange(pos.getCenter().relative(dir, 0.5).y - mc.player.getY()))
            //高度检测 针对于放置比自己低太多的方块
            .filter(dir -> pos.getCenter().relative(dir, 0.5).distanceTo(mc.player.getEyePosition()) <= pri.dSetPrintingRange.get())
            //距离检测

            .collect(Collectors.toList());

    }
    public static List<Direction> getDirs(BlockPos pos) {
//        if (!mc.world.getBlockState(pos).isReplaceable()){
//            return new ArrayList<>();
//        }
        List<Direction> validDirs = getValidDirs(pos);
        return validDirs.stream()
//            .filter(dir -> {
//                BlockPos offset = pos.offset(dir);
//                BlockState bs = mc.world.getBlockState(offset);
//                if (pri.bSetAirPlace.get()) return true;
//                if (pri.bSetLiquidInt.get() && bs.getBlock() instanceof FluidBlock) return true;
//                return !bs.isReplaceable();
//            })
            .filter(dir -> !mc.level.getBlockState(pos.relative(dir)).canBeReplaced())
            //不可被替换
            .filter(dir -> (mc.level.getBlockState(pos).isAir()) || !mc.level.getBlockState(pos).isFaceSturdy(mc.level, pos, dir, SupportType.FULL))
            //方块自身阻挡检测
            .filter(dir -> pri.dRangeSetPrintingYRange.get().isInRange(pos.getCenter().relative(dir, 0.5).y - mc.player.getY()))
            //高度检测 针对于放置比自己低太多的方块
            .filter(dir -> SeijaUtil.isSneak() || !isCanUseBlock(pos.relative(dir), mc.level.getBlockState(pos.relative(dir)), mc.level))
            //不可交互
            .filter(dir -> pos.getCenter().relative(dir, 0.5).distanceTo(mc.player.getEyePosition()) <= pri.dSetPrintingRange.get())
            //距离检测
            .filter(dir-> mc.level.getWorldBorder().isWithinBounds(pos.relative(dir)))
            //边境
            .collect(Collectors.toList());
    }


    public static void illegalRotate(RotationData data) {
        if (pri.bSetIllegalRotate.get() && data != null) {
//            if (mc.isInSingleplayer()) {
//                mc.player.lastYaw = (float) data.yaw();
//                PlayerMoveC2SPacket packet = new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), (float) data.yaw(),
//                    (float) data.pitch(), mc.player.isOnGround(),mc.player.horizontalCollision);
//                mc.player.networkHandler.sendPacket(packet);
//            } else
            mc.player.connection.send(new ServerboundMovePlayerPacket.Rot((float) data.yaw(), (float) data.pitch(), mc.player.onGround(), mc.player.horizontalCollision));
            mc.player.connection.send(new ServerboundMovePlayerPacket.Rot((float) data.yaw(), (float) data.pitch(), mc.player.onGround(), mc.player.horizontalCollision));

        }
    }

    public static BlockHitResult getHitRes(BlockPos pos, Direction dir, Vec3 hitVec) {
        Vec3 eyes = PredictUtility.getPredPlayerVec().relative(Direction.UP, SeijaUtil.getEyeHeight());
        boolean inside = eyes.x > (double) pos.getX() && eyes.x < (double) (pos.getX() + 1) && eyes.y > (double) pos.getY() && eyes.y < (double) (pos.getY() + 1) && eyes.z > (double) pos.getZ() && eyes.z < (double) (pos.getZ() + 1);
        return new BlockHitResult(hitVec, dir, pos, inside);
    }

    public static List<Direction> getValidDirs(BlockPos pos) {
        List<Direction> values = new ArrayList<>(List.of(Direction.values()));

        if (pri.bSetStrictDir.get()) {
            List<Direction> directions = canTorchFac(pos);
            for (Direction direction : directions) {
                values.remove(direction);
            }
        }
        return values;
    }

    public static List<Direction> canTorchFac(BlockPos pos) {
        List<Direction> list = new LinkedList<>();
        if (mc.player.getEyePosition().y() < pos.getY())
            list.add(Direction.DOWN);
        if (mc.player.getEyePosition().y() > pos.getY() + 1/*||pos.getY()<=mc.player.getEyePos().getY()+1*/)
            list.add(Direction.UP);
        if (mc.player.getEyePosition().x() < pos.getX())
            list.add(Direction.WEST);
        if (mc.player.getEyePosition().x() > pos.getX() + 1)
            list.add(Direction.EAST);
        if (mc.player.getEyePosition().z() < pos.getZ())
            list.add(Direction.NORTH);
        if (mc.player.getEyePosition().z() > pos.getZ() + 1)
            list.add(Direction.SOUTH);
        return list;
    }

    public static boolean canPlaceIn(BlockPos pos) {
        Block block = mc.level.getBlockState(pos).getBlock();
        return isCanPlaceInBlock(block);
    }

    public static boolean isCanPlaceInBlock(Block block) {
        return block instanceof LiquidBlock || block instanceof AirBlock || block instanceof BaseFireBlock;
    }

    public static List<BlockPos> getSphere(BlockPos centerPos, int radius, int height) {
        ArrayList<BlockPos> blocks = new ArrayList<BlockPos>();
        for (int i = centerPos.getX() - radius; i < centerPos.getX() + radius; ++i) {
            for (int j = centerPos.getY() - height; j < centerPos.getY() + height; ++j) {
                for (int k = centerPos.getZ() - radius; k < centerPos.getZ() + radius; ++k) {
                    BlockPos pos = new BlockPos(i, j, k);
                    if (!((double) radius > SeijaUtil.distanceBetween(centerPos, pos)) || blocks.contains(pos))
                        continue;
                    blocks.add(pos);
                }
            }
        }
        return blocks;
    }
    //区域内方块迭代 注意down向下要负数
    public static Iterable<BlockPos> sphereIterate(BlockPos centerPos, int radius, int up,int down){
        return BlockPos.betweenClosed(centerPos.getX()-radius,centerPos.getY()+down,centerPos.getZ()-radius
        ,centerPos.getX()+radius,centerPos.getY()+up,centerPos.getZ()+radius);
    }
    public static boolean blockposFilter(BlockPos pos){
        if (!DataManager.getRenderLayerRange().isPositionWithinRange(pos))
            return false;
        // 投影内不可见
        if (BlockUtil.isInBlackList(pos))
            return false;
        //黑名单内
        if (!BlockUtil.isValidState(BlockReplaceUtils.INSTANCE.getScheState(pos), pos))
            return false;
        //床头/上门无法放置
        if (!SurfaceUtil.surfaceCheck(pos, pri.iSetSurfaceSize.get()))
            return false;
        //表面模式检测
        return true;
    }
    //可以交互的方块列表
    public static List<Class<? extends Block>> canUseBlcks = new ArrayList<>(Arrays.asList(
        AbstractChestBlock.class, AbstractFurnaceBlock.class, CraftingTableBlock.class,
        LeverBlock.class,
        DoorBlock.class, TrapDoorBlock.class, BedBlock.class, RedStoneWireBlock.class,
        ScaffoldingBlock.class,
        HopperBlock.class, EnchantingTableBlock.class, NoteBlock.class, JukeboxBlock.class,
        CakeBlock.class,
        FenceGateBlock.class, BrewingStandBlock.class, DragonEggBlock.class, CommandBlock.class,
        BeaconBlock.class, AnvilBlock.class, ComparatorBlock.class, RepeaterBlock.class,
        DropperBlock.class, DispenserBlock.class, ShulkerBoxBlock.class, LecternBlock.class,
        FlowerPotBlock.class, BarrelBlock.class, BellBlock.class, SmithingTableBlock.class,
        LoomBlock.class, CartographyTableBlock.class, GrindstoneBlock.class,
        StonecutterBlock.class, SignBlock.class, AbstractCandleBlock.class));

    public static boolean isCanUseBlock(BlockPos p, BlockState state, Level world) {
        Block block = state.getBlock();
        for (Class<? extends Block> canUseBlck : canUseBlcks) {
            if (canUseBlck.isInstance(block)) {
                return true;
            }
        }
        return state.getMenuProvider(world, p) != null;
//        if (true)return false;//test
//        return (block instanceof AbstractRedstoneGateBlock
//            || block instanceof BlockWithEntity
//            || block instanceof ScaffoldingBlock
//            || block instanceof DoorBlock
//            || block instanceof TrapdoorBlock
//            || block instanceof FenceGateBlock
//            || block instanceof WallMountedBlock
//            || block instanceof ChestBlock
//            || block instanceof AnvilBlock
//            || block instanceof CraftingTableBlock
//            || block instanceof LoomBlock
//            || block instanceof EnderChestBlock
//            || block instanceof EnchantingTableBlock
//            || block instanceof SmithingTableBlock
//            || block instanceof GrindstoneBlock
//            || block instanceof StonecutterBlock
//
//        )
//            || (state.createScreenHandlerFactory(world, p) != null)
        //调用createScreenHandlerFactory检测对帧率影响极大


    }

    public static boolean isValidState(BlockState needState, BlockPos pos) {
        if (needState.getBlock() instanceof BedBlock) {
            return needState.getValue(BlockStateProperties.BED_PART) == BedPart.FOOT;
        } else if (needState.getBlock() instanceof DoorBlock
            || needState.getBlock() instanceof DoublePlantBlock) {
            return needState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER;
        }
        return true;
    }

    //Vec扩展 给定方块点击面上的中心Vec 将Vec向四周扩展
    //给的是扩展的量而不是扩张后的坐标
    private static RandomSource random = new LegacyRandomSource(2335353);

    private static double getRandomOffset() {
        return Printer.getINSTANCE().bSetRandomOffset.get() ?
            Mth.nextDouble(random, -0.15, 0.15) : 0;
    }

    public static Vec3 randomOffsetVec(Vec3 vec, Direction dir) {
        ArrayList<Direction> extendDir = getExtendDir(dir);
        return vec.relative(extendDir.get(0), getRandomOffset()).relative(extendDir.get(2), getRandomOffset());
    }

    public static ArrayList<Vec3> getExtendVec(Direction clickDir, boolean corner) {

        ArrayList<Direction> extendDir = getExtendDir(clickDir);
        ArrayList<Vec3> offVec = new ArrayList<>();
        for (int i = 0; i < extendDir.size(); i++) {
            if (corner) {
                if (i == 0) {
                    offVec.add(randomOffsetVec(
                        new Vec3(0, 0, 0).relative(extendDir.get(i), 1)
                            .relative(extendDir.get(extendDir.size() - 1), 1)
                        , clickDir));
                } else offVec.add(randomOffsetVec(
                    new Vec3(0, 0, 0).relative(extendDir.get(i - 1), 1)
                        .relative(extendDir.get(i), 1)
                    , clickDir));
            } else {
                offVec.add(randomOffsetVec(new Vec3(0, 0, 0).relative(extendDir.get(i), 1), clickDir));
            }

        }
        return offVec;
    }

    public static ArrayList<Direction> getExtendDir(Direction dir) {
        ArrayList<Direction> directions = new ArrayList<>(List.of(Direction.values()));
        directions.remove(dir);
        directions.remove(dir.getOpposite());
        return directions;
    }

//    public static boolean isStuckPos(BlockPos pos) {
//        return SeijaUtil.intersectsWithEntity(new AABB(pos), entity -> !entity.isSpectator() && !(entity instanceof ItemEntity) && !(entity instanceof Arrow));
//    }



    /**
     * is in black list 判断某位置投影中的方块是否在黑名单内
     *
     * @param pos pos
     * @return {@link boolean}
     */
    public static boolean isInBlackList(BlockPos pos) {
        if (Printer.getINSTANCE().liSetBlackLists.get().contains(BlockReplaceUtils.INSTANCE.getScheState(pos).getBlock())) {
            return true;
        }
        for (PosInfo info : Printer.getINSTANCE().blackList) {
            if (info.pos().equals(pos)) {
                return true;
            }
        }
        return false;
    }

    //    public static Set<BlockPos> getSurface(BlockPos pos, int c) {
//        HashSet<BlockPos> pos1 = new LinkedHashSet<>();
//        pos1.add(pos);
//        for (int i = 0; i < c; i++) {
//            pos1 = getSurface(pos1);
//        }
//        return pos1;
//    }
//
//    private static HashSet<BlockPos> getSurface(HashSet<BlockPos> set) {
//        HashSet<BlockPos> poss = new HashSet<>();
//        for (BlockPos blockPos : set) {
//            for (Direction dir : Direction.values()) {
//                poss.add(blockPos.offset(dir));
//            }
//        }
//        return poss;
//    }


}

