/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.util;

import com.kijinseija.seija_printer.utils.player.Rotations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class RotationUtils {
	private static final Minecraft mc = Minecraft.getInstance();
	public static Vec3 getEyesPos()
	{
		LocalPlayer player = mc.player;

		return new Vec3(player.getX(),
			player.getY() + player.getEyeHeight(player.getPose()),
			player.getZ());
	}

	public static float getAngleDifference(final float a, final float b) {
		return ((((a - b) % 360F) + 540F) % 360F) - 180F;
	}

	public static double getRotationDifference(final Rotation a, final Rotation b) {
		return Math.hypot(getAngleDifference(a.yaw, b.yaw), a.getPitch() - b.getPitch());
	}

	public static Rotation toRotation(final Vec3 vec, final boolean predict) {
		final Vec3 eyesPos = new Vec3(mc.player.getX(), mc.player.getBoundingBox().minY +
			mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ());

		if(predict) {
			if(mc.player.onGround()) {
				eyesPos.add(mc.player.getDeltaMovement().x, 0.0, mc.player.getDeltaMovement().z);
			}else eyesPos.add(mc.player.getDeltaMovement().x, mc.player.getDeltaMovement().y, mc.player.getDeltaMovement().z);
		}

		final double diffX = vec.x - eyesPos.x;
		final double diffY = vec.y - eyesPos.y;
		final double diffZ = vec.z - eyesPos.z;

		return new Rotation(
			(float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F,
			(float) (-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ)))));
	}

	public static Vec3 getCenter(final AABB bb) {
		return new Vec3(bb.minX + (bb.maxX - bb.minX) * 0.5, bb.minY + (bb.maxY - bb.minY) * 0.5, bb.minZ + (bb.maxZ - bb.minZ) * 0.5);
	}

	public static Rotation limitAngleChange(final Rotation currentRotation, final Rotation targetRotation, final float turnSpeed) {
		final float yawDifference = getAngleDifference(targetRotation.getYaw(), currentRotation.getYaw());
		final float pitchDifference = getAngleDifference(targetRotation.getPitch(), currentRotation.getPitch());

		return new Rotation(
			currentRotation.getYaw() + (yawDifference > turnSpeed ? turnSpeed : Math.max(yawDifference, -turnSpeed)),
			currentRotation.getPitch() + (pitchDifference > turnSpeed ? turnSpeed : Math.max(pitchDifference, -turnSpeed)
			));
	}

	public static Rotation getNeededRotations(Vec3 vec)
	{
		Vec3 eyesPos = getEyesPos();

		double diffX = vec.x - eyesPos.x;
		double diffY = vec.y - eyesPos.y;
		double diffZ = vec.z - eyesPos.z;

		double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

		float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
		float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));

		return new Rotation(yaw, pitch);
	}


	public static double getAngleToLookVec(Vec3 vec)
	{
		Rotation needed = getNeededRotations(vec);

		LocalPlayer player = mc.player;
		float currentYaw = Mth.wrapDegrees(player.getYRot());
		float currentPitch = Mth.wrapDegrees(player.getXRot());

		float diffYaw = currentYaw - needed.yaw;
		float diffPitch = currentPitch - needed.pitch;

		return Math.sqrt(diffYaw * diffYaw + diffPitch * diffPitch);
	}

	public static final class Rotation
	{
		private float yaw;
		private float pitch;

		public Rotation(float yaw, float pitch)
		{
			this.yaw = Mth.wrapDegrees(yaw);
			this.pitch = Mth.wrapDegrees(pitch);
		}

		public Rotation(double yaw, double pitch)
		{
			this.yaw = Mth.wrapDegrees((float)yaw);
			this.pitch = Mth.wrapDegrees((float)pitch);
		}

		public float getYaw()
		{
			return yaw;
		}

		public float getPitch()
		{
			return pitch;
		}

		public void setYaw(float yaw) {
			this.yaw = yaw;
		}

		public void setPitch(float pitch) {
			this.pitch = pitch;
		}
	}
}
