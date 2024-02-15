package com.kijinseija.seija_printer.printer.util;

import com.kijinseija.seija_printer.printer.Printer;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import meteordevelopment.meteorclient.utils.player.Rotations;
import net.minecraft.block.*;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;

public class BlockUtil {
    private static Printer pri = Printer.INSTANCE;
    private static MinecraftClient mc = MinecraftClient.getInstance();


    public static List<Direction> getInteractDir(BlockPos pos) {
        return canTorchFac(pos).stream()
            .filter(dir -> canPlaceIn(pos.offset(dir)))
            .filter(dir -> mc.player.getY() - pos.toCenterPos().offset(dir, 0.5).y < pri.printingYDistance.get())
            //高度检测 针对于放置比自己低太多的方块
            .filter(dir -> pos.toCenterPos().offset(dir, 0.5).distanceTo(mc.player.getEyePos()) <= pri.printingRange.get())
            //距离检测
            .collect(Collectors.toList());

    }

    public static void interactBlock(BlockPos pos, Direction dir) {
        Vec3d hitVec = pos.toCenterPos().offset(dir, 0.5);
        Runnable r = () -> {
            if (pri.packetPlace.get()) {
                mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, getHitRes(pos, dir, hitVec), SeijaUtil.getSequence()));
                mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            } else {
                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, getHitRes(pos, dir, hitVec));
            }
            pri.blackList.add(RenderHelper.getBlackInfo(pos));
            pri.renderList.add(RenderHelper.getBlackInfo(pos));
        };
        if (pri.packetRotate.get()) {
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround((float) SeijaUtil.getYaw(hitVec), (float) SeijaUtil.getPitch(hitVec), mc.player.isOnGround()));
            r.run();
        } else
            Rotations.rotate(SeijaUtil.getYaw(hitVec), SeijaUtil.getPitch(hitVec), r);
    }

    public static List<Direction> getDirs(BlockPos pos) {

        List<Direction> validDirs = getValidDirs(pos);
        return validDirs.stream()
            .filter(dir -> {
                BlockPos offset = pos.offset(dir);
                BlockState bs = mc.world.getBlockState(offset);
                if (pri.airPlace.get()) return true;
                if (pri.liquidInt.get() && bs.getBlock() instanceof FluidBlock) return true;
                return bs.isSolid();
            })
            .filter(dir -> mc.player.getY() - pos.toCenterPos().offset(dir, 0.5).y < pri.printingYDistance.get())
            //高度检测 针对于放置比自己低太多的方块
            .filter(dir -> pri.sneak.get() || !isCanUseBlock(pos.offset(dir), mc.world.getBlockState(pos.offset(dir)), mc.world))
            //不可交互
            .filter(dir -> pos.toCenterPos().offset(dir, 0.5).distanceTo(mc.player.getEyePos()) <= pri.printingRange.get())
            //距离检测
            .collect(Collectors.toList());
    }

    public static void placeBlock(PlaceData data) {
        BlockPos pos = data.pos();
        Direction dir = data.dir();
        Vec3d hitVec = data.hitVec();
        Runnable r = () -> {
            boolean sneakToggle = false;
            if (pri.sneak.get()) {
                if (!mc.player.isSneaking()) {
                    sneakToggle = true;
                    mc.player.setSneaking(true);
                    mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
                }
            }
            if (pri.packetPlace.get()) {
                mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, getHitRes(pos, dir, hitVec), SeijaUtil.getSequence()));
                mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            } else {
                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, getHitRes(pos, dir, hitVec));
            }

            if (sneakToggle) {
                mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
                mc.player.setSneaking(false);
            }
            pri.blackList.add(RenderHelper.getBlackInfo(pos.offset(dir)));
            pri.renderList.add(RenderHelper.getBlackInfo(pos.offset(dir)));
        };
        if (pri.packetRotate.get()) {
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround((float) SeijaUtil.getYaw(hitVec), (float) SeijaUtil.getPitch(hitVec), mc.player.isOnGround()));
            r.run();
        } else
            Rotations.rotate(SeijaUtil.getYaw(hitVec), SeijaUtil.getPitch(hitVec), r);

    }

    public static BlockHitResult getHitRes(BlockPos pos, Direction dir, Vec3d hitVec) {
        Vec3d eyes = mc.player.getEyePos();
        boolean inside = eyes.x > (double) pos.getX() && eyes.x < (double) (pos.getX() + 1) && eyes.y > (double) pos.getY() && eyes.y < (double) (pos.getY() + 1) && eyes.z > (double) pos.getZ() && eyes.z < (double) (pos.getZ() + 1);
        return new BlockHitResult(hitVec, dir, pos, inside);
    }

    public static Direction[] getEntityFacingOrder(float yaw, float pitch) {
        Direction direction3;
        float f = pitch * ((float) Math.PI / 180);
        float g = -yaw * ((float) Math.PI / 180);
        float h = MathHelper.sin(f);
        float i = MathHelper.cos(f);
        float j = MathHelper.sin(g);
        float k = MathHelper.cos(g);
        boolean bl = j > 0.0f;
        boolean bl2 = h < 0.0f;
        boolean bl3 = k > 0.0f;
        float l = bl ? j : -j;
        float m = bl2 ? -h : h;
        float n = bl3 ? k : -k;
        float o = l * i;
        float p = n * i;
        Direction direction = bl ? Direction.EAST : Direction.WEST;
        Direction direction2 = bl2 ? Direction.UP : Direction.DOWN;
        Direction direction4 = direction3 = bl3 ? Direction.SOUTH : Direction.NORTH;
        if (l > n) {
            if (m > o) {
                return listClosest(direction2, direction, direction3);
            }
            if (p > m) {
                return listClosest(direction, direction3, direction2);
            }
            return listClosest(direction, direction2, direction3);
        }
        if (m > p) {
            return listClosest(direction2, direction3, direction);
        }
        if (o > m) {
            return listClosest(direction3, direction, direction2);
        }
        return listClosest(direction3, direction2, direction);
    }

    private static Direction[] listClosest(Direction first, Direction second, Direction third) {
        return new Direction[]{first, second, third, third.getOpposite(), second.getOpposite(), first.getOpposite()};
    }

    public static List<Direction> getValidDirs(BlockPos pos) {
        List<Direction> values = new ArrayList<>(List.of(Direction.values()));

        if (pri.strictDir.get()) {
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

    public static boolean isCanUseBlock(BlockPos p, BlockState state, World world) {
        Block block = state.getBlock();
        return block instanceof AbstractRedstoneGateBlock
            ||block instanceof BlockWithEntity
            ||block instanceof DoorBlock
            ||block instanceof TrapdoorBlock
            ||block instanceof FenceGateBlock
            ||block instanceof WallMountedBlock;
//        List<Direction> dirs = getValidDirs(p);
//        if (dirs.isEmpty()) {
//            return true;
//        }
//        Direction direction = dirs.get(0);
//
//        BlockHitResult hitRes = getHitRes(p.offset(direction), direction.getOpposite(), p.toCenterPos().offset(direction, 0.5));
//
//        return state.onUse(world, mc.player, Hand.MAIN_HAND, hitRes) != ActionResult.PASS;
    }

    public static boolean isValidState(BlockState needState,BlockPos pos) {
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
    public static ArrayList<Vec3d> getExtendVec(Vec3d clickVec, Direction clickDir, boolean corner) {
        ArrayList<Direction> extendDir = getExtendDir(clickDir);
        ArrayList<Vec3d> offVec = new ArrayList<>();
        for (int i = 0; i < extendDir.size(); i++) {
            if (corner) {
                if (i == 0) {
                    offVec.add(new Vec3d(0, 0, 0).offset(extendDir.get(i), 1)
                        .offset(extendDir.get(extendDir.size() - 1), 1));
                } else offVec.add(new Vec3d(0, 0, 0).offset(extendDir.get(i - 1), 1)
                    .offset(extendDir.get(i), 1));
            } else {
                offVec.add(new Vec3d(0, 0, 0).offset(extendDir.get(i), 1));
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

    public static boolean surfaceCheck(BlockPos pos, int c) {
        if (c == 0) return true;
        Set<BlockPos> surface = getSurface(pos, c);
        surface.remove(pos);
        for (BlockPos blockPos : surface) {
            if (!BlockReplaceUtils.INSTANCE.getScheState(blockPos).isSolid()) return true;
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


    public static Set<BlockPos> getSurface(BlockPos pos, int c) {
        Set<BlockPos> result = new HashSet<>();
        result.add(pos);
        for (int i = 0; i < c; i++) {
            result = getSurface(result);
        }
        return result;
    }

    private static Set<BlockPos> getSurface(Set<BlockPos> set) {
        return set.stream()
            .flatMap(blockPos -> Arrays.stream(Direction.values())
                .map(blockPos::offset)
                .toList().stream())
            .collect(Collectors.toSet());
    }
}


