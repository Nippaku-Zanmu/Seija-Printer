package com.kijinseija.seija_printer.print_main.printer.util;

import com.kijinseija.seija_printer.mixin.ClientWorldAccessor;
import com.kijinseija.seija_printer.print_main.modules.Printer;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import meteordevelopment.meteorclient.mixin.EntityTrackingSectionAccessor;
import meteordevelopment.meteorclient.mixin.SectionedEntityCacheAccessor;
import meteordevelopment.meteorclient.mixin.SimpleEntityLookupAccessor;
import meteordevelopment.meteorclient.mixin.WorldAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.*;
import net.minecraft.world.entity.EntityLookup;
import net.minecraft.world.entity.EntityTrackingSection;
import net.minecraft.world.entity.SectionedEntityCache;
import net.minecraft.world.entity.SimpleEntityLookup;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class SeijaUtil {
    public static Printer pri = Printer.getINSTANCE();
    static MinecraftClient mc = MinecraftClient.getInstance();

    public static boolean isSneak(){
        return mc.player.isSneaking()||pri.bSetSneak.get();
    }

    public static double getEyeHeight() {
        double eyeHeight;
        if (pri.bSetSneak.get()) {

            if (mc.player.getEyeHeight(mc.player.getPose()) < 1) {
                eyeHeight = mc.player.getEyeHeight(mc.player.getPose());
            } else eyeHeight = mc.player.getEyeHeight(EntityPose.CROUCHING);
        } else
            eyeHeight = mc.player.getEyePos().y - mc.player.getPos().y;
        return eyeHeight;
    }

    public static double getYaw(Vec3d pos) {

        Vec3d pVec = PredictUtility.getPredPlayerVec();
        return mc.player.getYaw() + MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(pos.getZ() - pVec.getZ(), pos.getX() - pVec.getX())) - 90f - mc.player.getYaw());
    }

    public static double getPitch(Vec3d pos) {
        Vec3d pVec = PredictUtility.getPredPlayerVec();
        double eyeHeight = getEyeHeight();


        double diffX = pos.getX() - pVec.getX();
        double diffY = pos.getY() - (pVec.getY() + eyeHeight);
        double diffZ = pos.getZ() - pVec.getZ();

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        return mc.player.getPitch() + MathHelper.wrapDegrees((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)) - mc.player.getPitch());
    }

    public static int getSequence() {
        PendingUpdateManager sequence = ((ClientWorldAccessor) mc.world).getPendingUpdateManager().incrementSequence();
        return sequence.getSequence();
    }


    public static double distanceBetween(BlockPos pos1, BlockPos pos2) {
        double d = pos1.getX() - pos2.getX();
        double e = pos1.getY() - pos2.getY();
        double f = pos1.getZ() - pos2.getZ();
        return MathHelper.sqrt((float) (d * d + e * e + f * f));
    }

    public static boolean intersectsWithEntity(Box box, Predicate<Entity> predicate) {
        EntityLookup<Entity> entityLookup = ((WorldAccessor) mc.world).meteor$getEntityLookup();

        // Fast implementation using SimpleEntityLookup that returns on the first intersecting entity
        if (entityLookup instanceof SimpleEntityLookup<Entity> simpleEntityLookup) {
            SectionedEntityCache<Entity> cache = ((SimpleEntityLookupAccessor) simpleEntityLookup).meteor$getCache();
            LongSortedSet trackedPositions = ((SectionedEntityCacheAccessor) cache).meteor$getTrackedPositions();
            Long2ObjectMap<EntityTrackingSection<Entity>> trackingSections = ((SectionedEntityCacheAccessor) cache).meteor$getTrackingSections();

            int i = ChunkSectionPos.getSectionCoord(box.minX - 2);
            int j = ChunkSectionPos.getSectionCoord(box.minY - 2);
            int k = ChunkSectionPos.getSectionCoord(box.minZ - 2);
            int l = ChunkSectionPos.getSectionCoord(box.maxX + 2);
            int m = ChunkSectionPos.getSectionCoord(box.maxY + 2);
            int n = ChunkSectionPos.getSectionCoord(box.maxZ + 2);

            for (int o = i; o <= l; o++) {
                long p = ChunkSectionPos.asLong(o, 0, 0);
                long q = ChunkSectionPos.asLong(o, -1, -1);
                LongBidirectionalIterator longIterator = trackedPositions.subSet(p, q + 1).iterator();

                while (longIterator.hasNext()) {
                    long r = longIterator.nextLong();
                    int s = ChunkSectionPos.unpackY(r);
                    int t = ChunkSectionPos.unpackZ(r);

                    if (s >= j && s <= m && t >= k && t <= n) {
                        EntityTrackingSection<Entity> entityTrackingSection = trackingSections.get(r);

                        if (entityTrackingSection != null && entityTrackingSection.getStatus().shouldTrack()) {
                            for (Entity entity : ((EntityTrackingSectionAccessor) entityTrackingSection).<Entity>meteor$getCollection()) {
                                if (entity.getBoundingBox().intersects(box) && predicate.test(entity)) return true;
                            }
                        }
                    }
                }
            }

            return false;
        }

        // Slow implementation that loops every entity if for some reason the EntityLookup implementation is changed
        AtomicBoolean found = new AtomicBoolean(false);

        entityLookup.forEachIntersects(box, entity -> {
            if (!found.get() && predicate.test(entity)) found.set(true);
        });

        return found.get();
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

    public static Direction[] getPlacementDirections(Vec3d clickVec, BlockPos placePos, Direction offsetDir) {
        int i;
        Direction[] directions = getEntityFacingOrder((float) getYaw(clickVec), (float) getPitch(clickVec));
        if (BlockUtil.canPlaceIn(placePos.offset(offsetDir))) {
            return directions;
        }
        Direction direction = offsetDir.getOpposite();
        for (i = 0; i < directions.length && directions[i] != direction.getOpposite(); ++i) {
        }
        if (i > 0) {
            System.arraycopy(directions, 0, directions, 1, i);
            directions[0] = direction.getOpposite();
        }
        return directions;
    }
}
