/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.events.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.kijinseija.seija_printer.render.ShapeMode;
import com.kijinseija.seija_printer.settings.core.Color;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;

/** Render callback payload and a small immediate-style shape facade. */
public final class Render3DEvent {
    public final LevelRenderContext context;
    public final PoseStack matrices;
    public final Renderer renderer;

    public Render3DEvent(LevelRenderContext context) {
        this.context = context;
        this.matrices = context.poseStack();
        this.renderer = new Renderer(context);
    }

    /** Flushes the shared level buffer after all modules have emitted geometry. */
    public void flush() {
        context.bufferSource().endBatch();
    }

    public static final class Renderer {
        private final LevelRenderContext context;
        private final PoseStack matrices;
        private final Vec3 cameraPosition;

        private Renderer(LevelRenderContext context) {
            this.context = context;
            this.matrices = context.poseStack();
            Camera camera = context.gameRenderer().getMainCamera();
            this.cameraPosition = camera == null ? Vec3.ZERO : camera.position();
        }

        public void box(AABB box, Color sideColor, Color lineColor, ShapeMode mode, int ignoredFlags) {
            if (box == null || mode == null) return;
            if (mode == ShapeMode.Sides || mode == ShapeMode.Both) {
                drawFaces(box, sideColor);
            }
            if (mode == ShapeMode.Lines || mode == ShapeMode.Both) {
                drawEdges(box, lineColor == null ? sideColor : lineColor);
            }
        }

        public void boxSides(double minX, double minY, double minZ,
                             double maxX, double maxY, double maxZ,
                             Color color, int flags) {
            box(new AABB(minX, minY, minZ, maxX, maxY, maxZ), color, null, ShapeMode.Sides, flags);
        }

        public void boxLines(double minX, double minY, double minZ,
                             double maxX, double maxY, double maxZ,
                             Color color, int flags) {
            box(new AABB(minX, minY, minZ, maxX, maxY, maxZ), null, color, ShapeMode.Lines, flags);
        }

        public void line(double x1, double y1, double z1,
                         double x2, double y2, double z2, Color color) {
            if (color == null) return;
            VertexConsumer vertices = context.bufferSource().getBuffer(RenderTypes.lines());
            PoseStack.Pose pose = matrices.last();
            float ax = relativeX(x1), ay = relativeY(y1), az = relativeZ(z1);
            float bx = relativeX(x2), by = relativeY(y2), bz = relativeZ(z2);
            float dx = bx - ax, dy = by - ay, dz = bz - az;
            float length = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (length < 1.0e-5f) return;
            float nx = dx / length, ny = dy / length, nz = dz / length;
            int argb = color.argb();
            vertices.addVertex(pose, ax, ay, az).setColor(argb).setNormal(pose, nx, ny, nz).setLineWidth(1.0f);
            vertices.addVertex(pose, bx, by, bz).setColor(argb).setNormal(pose, nx, ny, nz).setLineWidth(1.0f);
        }

        /** Compatibility no-op for older renderer call sites. */
        public void render(PoseStack ignored) {
        }

        private void drawFaces(AABB box, Color color) {
            if (color == null) return;
            VertexConsumer vertices = context.bufferSource().getBuffer(RenderTypes.debugFilledBox());
            PoseStack.Pose pose = matrices.last();
            float x1 = relativeX(box.minX), y1 = relativeY(box.minY), z1 = relativeZ(box.minZ);
            float x2 = relativeX(box.maxX), y2 = relativeY(box.maxY), z2 = relativeZ(box.maxZ);
            int argb = color.argb();
            vertex(vertices, pose, x1, y1, z1, argb); vertex(vertices, pose, x2, y1, z1, argb);
            vertex(vertices, pose, x2, y1, z2, argb); vertex(vertices, pose, x1, y1, z2, argb);
            vertex(vertices, pose, x1, y2, z1, argb); vertex(vertices, pose, x1, y2, z2, argb);
            vertex(vertices, pose, x2, y2, z2, argb); vertex(vertices, pose, x2, y2, z1, argb);
            vertex(vertices, pose, x1, y1, z2, argb); vertex(vertices, pose, x2, y1, z2, argb);
            vertex(vertices, pose, x2, y2, z2, argb); vertex(vertices, pose, x1, y2, z2, argb);
            vertex(vertices, pose, x2, y1, z1, argb); vertex(vertices, pose, x1, y1, z1, argb);
            vertex(vertices, pose, x1, y2, z1, argb); vertex(vertices, pose, x2, y2, z1, argb);
            vertex(vertices, pose, x1, y1, z1, argb); vertex(vertices, pose, x1, y1, z2, argb);
            vertex(vertices, pose, x1, y2, z2, argb); vertex(vertices, pose, x1, y2, z1, argb);
            vertex(vertices, pose, x2, y1, z2, argb); vertex(vertices, pose, x2, y1, z1, argb);
            vertex(vertices, pose, x2, y2, z1, argb); vertex(vertices, pose, x2, y2, z2, argb);
        }

        private void drawEdges(AABB box, Color color) {
            if (color == null) return;
            VertexConsumer vertices = context.bufferSource().getBuffer(RenderTypes.lines());
            PoseStack.Pose pose = matrices.last();
            AABB relative = new AABB(
                box.minX - cameraPosition.x, box.minY - cameraPosition.y, box.minZ - cameraPosition.z,
                box.maxX - cameraPosition.x, box.maxY - cameraPosition.y, box.maxZ - cameraPosition.z
            );
            ShapeRenderer.renderShape(matrices, vertices, Shapes.create(relative), 0, 0, 0, color.argb(), 1.0f);
        }

        private static void vertex(VertexConsumer vertices, PoseStack.Pose pose,
                                   float x, float y, float z, int color) {
            vertices.addVertex(pose, x, y, z).setColor(color);
        }

        private float relativeX(double x) { return (float) (x - cameraPosition.x); }
        private float relativeY(double y) { return (float) (y - cameraPosition.y); }
        private float relativeZ(double z) { return (float) (z - cameraPosition.z); }
    }
}
