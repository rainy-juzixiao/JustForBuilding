/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.preview;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.rainy_juzixiao.justforbuilding.build.BuildDirection;
import net.rainy_juzixiao.justforbuilding.build.RectAnchor;
import net.rainy_juzixiao.justforbuilding.build.RectGeometry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class RectPreviewClient {

    private static final int MAX_PREVIEW_BLOCKS = 4096;

    private static boolean active;
    private static int length;
    private static int width;
    private static boolean hollow;
    private static RectAnchor anchor;

    private static final List<BlockPos> POSITIONS = new ArrayList<>();

    private RectPreviewClient() {
    }

    public static void update(boolean active, int length, int width, boolean hollow, RectAnchor anchor) {
        RectPreviewClient.active = active;
        RectPreviewClient.length = length;
        RectPreviewClient.width = width;
        RectPreviewClient.hollow = hollow;
        RectPreviewClient.anchor = anchor;
        if (!active) {
            POSITIONS.clear();
        }
    }

    public static void tick(Minecraft minecraft) {
        POSITIONS.clear();
        if (!active || minecraft.player == null || minecraft.level == null) {
            return;
        }
        HitResult hit = minecraft.hitResult;
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) {
            return;
        }
        BlockHitResult blockHit = (BlockHitResult) hit;
        BlockPos start = blockHit.getBlockPos().relative(blockHit.getDirection());
        boolean boundaryOnly = hollow;
        if (!boundaryOnly && length * width > MAX_PREVIEW_BLOCKS) {
            boundaryOnly = true;
        }
        RectGeometry.fillPositions(start, BuildDirection.fromYRot(minecraft.player.yRot),
                length, width, boundaryOnly, anchor, POSITIONS);
    }

    public static void render(PoseStack poseStack, Camera camera) {
        if (POSITIONS.isEmpty()) {
            return;
        }
        Vec3 cam = camera.getPosition();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());
        for (BlockPos pos : POSITIONS) {
            LevelRenderer.renderLineBox(poseStack, consumer,
                    pos.getX() - cam.x, pos.getY() - cam.y, pos.getZ() - cam.z,
                    pos.getX() + 1 - cam.x, pos.getY() + 1 - cam.y, pos.getZ() + 1 - cam.z,
                    1.0F, 1.0F, 1.0F, 0.4F);
        }
        bufferSource.endBatch(RenderType.lines());
    }
}
