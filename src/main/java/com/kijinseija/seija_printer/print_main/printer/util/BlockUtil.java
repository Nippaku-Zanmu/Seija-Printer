package com.kijinseija.seija_printer.print_main.printer.util;

import com.kijinseija.seija_printer.print_main.modules.Printer;
import com.kijinseija.seija_printer.print_main.printer.util.records.PlaceData;
import com.kijinseija.seija_printer.print_main.printer.util.records.PosInfo;
import com.kijinseija.seija_printer.print_main.printer.util.records.RotationData;
import meteordevelopment.meteorclient.utils.player.Rotations;
import net.minecraft.block.*;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;

public class BlockUtil {
    private static Printer pri = Printer.getINSTANCE();
    private static MinecraftClient mc = MinecraftClient.getInstance();


    public static List<Direction> getInteractDir(BlockPos pos) {
        return (pri.bSetStrictDir.get() ? canTorchFac(pos) : Arrays.asList(Direction.values())).stream()
            .filter(dir -> (!pri.bSetStrictDir.get()) || canPlaceIn(pos.offset(dir)))
            .filter(dir -> mc.player.getY() - pos.toCenterPos().offset(dir, 0.5).y < pri.dSetPrintingYDistance.get())
            //高度检测 针对于放置比自己低太多的方块
            .filter(dir -> pos.toCenterPos().offset(dir, 0.5).distanceTo(mc.player.getEyePos()) <= pri.dSetPrintingRange.get())
            //距离检测
            .collect(Collectors.toList());

    }

    public static void interactBlock(PlaceData data) {
        Vec3d hitVec = data.hitVec();
        final BlockPos.Mutable pos = new BlockPos.Mutable(data.pos().getX(), data.pos().getY(), data.pos().getZ());
        Direction dir = data.dir();
        Runnable r = () -> {
            if (pri.bSetIllegalRotate.get() && data.exRotateData() != null) {
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround((float) data.exRotateData().yaw(), (float) data.exRotateData().pitch(), mc.player.isOnGround(),mc.player.horizontalCollision));
            }//非法转头
            if (pri.bSetPacketPlace.get()) {
                mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, getHitRes(pos, dir, hitVec), SeijaUtil.getSequence()));
                mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            } else {
                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, getHitRes(pos, dir, hitVec));
            }

            PosInfo blackInfo = RenderHelper.getBlackInfo(pos, dir, hitVec, false);
            pri.blackList.add(blackInfo);
            RenderUtil.renderList.add(blackInfo);
        };
        if (pri.bSetRotate.get()) {
            if (pri.bSetPacketRotate.get()) {
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround((float) SeijaUtil.getYaw(hitVec), (float) SeijaUtil.getPitch(hitVec), mc.player.isOnGround(),mc.player.horizontalCollision));
                r.run();
            } else
                Rotations.rotate(SeijaUtil.getYaw(hitVec), SeijaUtil.getPitch(hitVec), r);
        } else r.run();


    }

    public static List<Direction> getSortedDirs(BlockPos pos) {
        return pri.bSetSortDir.get()?DirSorter.sort(getDirs(pos), pos):getDirs(pos);
//        if ()
//            return ;
//        return ;
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
            .filter(dir -> !mc.world.getBlockState(pos.offset(dir)).isReplaceable())
            //不可被替换
            .filter(dir -> (mc.world.getBlockState(pos).isAir()) || !mc.world.getBlockState(pos).isSideSolid(mc.world, pos, dir, SideShapeType.FULL))
            //方块自身阻挡检测
            .filter(dir -> mc.player.getY() - pos.toCenterPos().offset(dir, 0.5).y < pri.dSetPrintingYDistance.get())
            //高度检测 针对于放置比自己低太多的方块
            .filter(dir -> SeijaUtil.isSneak() || !isCanUseBlock(pos.offset(dir), mc.world.getBlockState(pos.offset(dir)), mc.world))
            //不可交互
            .filter(dir -> pos.toCenterPos().offset(dir, 0.5).distanceTo(mc.player.getEyePos()) <= pri.dSetPrintingRange.get())
            //距离检测
            .collect(Collectors.toList());
    }

    public static void placeBlock(PlaceData data) {
        final BlockPos.Mutable pos = new BlockPos.Mutable(data.pos().getX(), data.pos().getY(), data.pos().getZ());


        Direction dir = data.dir();
        Vec3d hitVec = data.hitVec();
        final boolean isBucket = mc.player.getMainHandStack().getItem() instanceof BucketItem;

        Runnable r = () -> {
            boolean sneakToggle = false;
            if (pri.bSetSneak.get()) {
                if (!mc.player.isSneaking()) {
                    sneakToggle = true;
                    mc.player.setSneaking(true);
                    mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
                }
            }
            //非法转头
            illegalRotate(data.exRotateData());
            if (isBucket) {
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            }
            if (pri.bSetPacketPlace.get()) {
                mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, getHitRes(pos, dir, hitVec), SeijaUtil.getSequence()));
                mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            } else {
                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, getHitRes(pos, dir, hitVec));
                mc.player.swingHand(Hand.MAIN_HAND);
            }

//            if (pri.illegalRotate.get() && data.exRotateData() != null) {
//                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround((float) data.exRotateData().yaw(), (float) data.exRotateData().pitch(), mc.player.isOnGround()));
//            }


            if (sneakToggle) {
                mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
                mc.player.setSneaking(false);
            }
            PosInfo blackInfo = RenderHelper.getBlackInfo(pos.offset(dir), dir, hitVec, true);
            pri.blackList.add(blackInfo);
            RenderUtil.renderList.add(blackInfo);
        };
        if (pri.bSetRotate.get() || isBucket) {
            if (pri.bSetPacketRotate.get()) {
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround((float) SeijaUtil.getYaw(hitVec), (float) SeijaUtil.getPitch(hitVec), mc.player.isOnGround(),mc.player.horizontalCollision));
                r.run();
            } else
                Rotations.rotate(SeijaUtil.getYaw(hitVec), SeijaUtil.getPitch(hitVec), r);
        } else {
            r.run();
        }

    }

    public static void illegalRotate(RotationData data) {
        if (pri.bSetIllegalRotate.get() && data != null) {
            if (mc.isInSingleplayer()) {
                mc.player.prevYaw = (float) data.yaw();
                PlayerMoveC2SPacket packet = new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), (float) data.yaw(),
                    (float) data.pitch(), mc.player.isOnGround(),mc.player.horizontalCollision);
                mc.player.networkHandler.sendPacket(packet);
            } else
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround((float) data.yaw(), (float) data.pitch(), mc.player.isOnGround(),mc.player.horizontalCollision));

        }
    }

    public static BlockHitResult getHitRes(BlockPos pos, Direction dir, Vec3d hitVec) {
        Vec3d eyes = PredictUtility.getPredPlayerVec().offset(Direction.UP, SeijaUtil.getEyeHeight());
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
        List<Direction> list = new ArrayList<>();
        if (mc.player.getEyePos().getY() < pos.getY())
            list.add(Direction.DOWN);
        if (mc.player.getEyePos().getY() > pos.getY() + 1/*||pos.getY()<=mc.player.getEyePos().getY()+1*/)
            list.add(Direction.UP);
        if (mc.player.getEyePos().getX() < pos.getX())
            list.add(Direction.WEST);
        if (mc.player.getEyePos().getX() > pos.getX() + 1)
            list.add(Direction.EAST);
        if (mc.player.getEyePos().getZ() < pos.getZ())
            list.add(Direction.NORTH);
        if (mc.player.getEyePos().getZ() > pos.getZ() + 1)
            list.add(Direction.SOUTH);
        return list;
    }

    public static boolean canPlaceIn(BlockPos pos) {
        Block block = mc.world.getBlockState(pos).getBlock();
        return isCanPlaceInBlock(block);
    }

    public static boolean isCanPlaceInBlock(Block block) {
        return block instanceof FluidBlock || block instanceof AirBlock || block instanceof AbstractFireBlock;
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

    public static List<Class<? extends Block>> canUseBlcks = new ArrayList<>(Arrays.asList(
        AbstractChestBlock.class, AbstractFurnaceBlock.class, CraftingTableBlock.class,
        LeverBlock.class,
        DoorBlock.class, TrapdoorBlock.class, BedBlock.class, RedstoneWireBlock.class,
        ScaffoldingBlock.class,
        HopperBlock.class, EnchantingTableBlock.class, NoteBlock.class, JukeboxBlock.class,
        CakeBlock.class,
        FenceGateBlock.class, BrewingStandBlock.class, DragonEggBlock.class, CommandBlock.class,
        BeaconBlock.class, AnvilBlock.class, ComparatorBlock.class, RepeaterBlock.class,
        DropperBlock.class, DispenserBlock.class, ShulkerBoxBlock.class, LecternBlock.class,
        FlowerPotBlock.class, BarrelBlock.class, BellBlock.class, SmithingTableBlock.class,
        LoomBlock.class, CartographyTableBlock.class, GrindstoneBlock.class,
        StonecutterBlock.class, AbstractSignBlock.class, AbstractCandleBlock.class));

    public static boolean isCanUseBlock(BlockPos p, BlockState state, World world) {
        Block block = state.getBlock();
        for (Class<? extends Block> canUseBlck : canUseBlcks) {
            if (canUseBlck.isInstance(block)) {
                return true;
            }
        }
        return state.createScreenHandlerFactory(world, p) != null;
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
            return needState.get(Properties.BED_PART) == BedPart.FOOT;
        } else if (needState.getBlock() instanceof DoorBlock
            || needState.getBlock() instanceof TallPlantBlock) {
            return needState.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER;
        }
        return true;
    }

    //Vec扩展 给定方块点击面上的中心Vec 将Vec向四周扩展
    //给的是扩展的量而不是扩张后的坐标
    private static Random random = new CheckedRandom(2335353);

    private static double getRandomOffset() {
        return Printer.getINSTANCE().bSetRandomOffset.get() ?
            MathHelper.nextDouble(random, -0.15, 0.15) : 0;
    }

    public static Vec3d randomOffsetVec(Vec3d vec, Direction dir) {
        ArrayList<Direction> extendDir = getExtendDir(dir);
        return vec.offset(extendDir.get(0), getRandomOffset()).offset(extendDir.get(2), getRandomOffset());
    }

    public static ArrayList<Vec3d> getExtendVec(Direction clickDir, boolean corner) {

        ArrayList<Direction> extendDir = getExtendDir(clickDir);
        ArrayList<Vec3d> offVec = new ArrayList<>();
        for (int i = 0; i < extendDir.size(); i++) {
            if (corner) {
                if (i == 0) {
                    offVec.add(randomOffsetVec(
                        new Vec3d(0, 0, 0).offset(extendDir.get(i), 1)
                            .offset(extendDir.get(extendDir.size() - 1), 1)
                        , clickDir));
                } else offVec.add(randomOffsetVec(
                    new Vec3d(0, 0, 0).offset(extendDir.get(i - 1), 1)
                        .offset(extendDir.get(i), 1)
                    , clickDir));
            } else {
                offVec.add(randomOffsetVec(new Vec3d(0, 0, 0).offset(extendDir.get(i), 1), clickDir));
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

    public static boolean isStuckPos(BlockPos pos) {
        return SeijaUtil.intersectsWithEntity(new Box(pos), entity -> !entity.isSpectator() && !(entity instanceof ItemEntity) && !(entity instanceof ArrowEntity));
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


