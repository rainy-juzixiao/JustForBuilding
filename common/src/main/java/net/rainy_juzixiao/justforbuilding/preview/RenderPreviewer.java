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
import net.rainy_juzixiao.justforbuilding.build.BuildContext;
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
public abstract class RenderPreviewer {

    protected static final int MAX_PREVIEW_BLOCKS = 4096;

    private boolean active;
    private boolean destroy;
    private BuildContext context;

    protected final List<BlockPos> positions = new ArrayList<>();

    public void update(boolean active, BuildContext context, boolean destroy) {
        this.active = active;
        this.destroy = destroy;
        this.context = context;
        if (!active) {
            positions.clear();
        }
    }

    public void tick(Minecraft minecraft) {
        positions.clear();
        if (!active || context == null || minecraft.player == null || minecraft.level == null) {
            return;
        }
        HitResult hit = minecraft.hitResult;
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) {
            return;
        }
        BlockHitResult blockHit = (BlockHitResult) hit;
        // 破坏模式下以点击的方块为起点，放置模式以外侧方块为起点
        BlockPos start = destroy ? blockHit.getBlockPos() : blockHit.getBlockPos().relative(blockHit.getDirection());
        computePositions(start, minecraft);
    }

    protected abstract void computePositions(BlockPos start, Minecraft minecraft);

    public void render(PoseStack poseStack, Camera camera) {
        if (positions.isEmpty()) {
            return;
        }
        Vec3 cam = camera.getPosition();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());
        // destroy 模式下渲染红色，否则白色
        float r = 1.0F;
        float g = destroy ? 0.0F : 1.0F;
        float b = destroy ? 0.0F : 1.0F;
        float a = 0.4F;
        for (BlockPos pos : positions) {
            LevelRenderer.renderLineBox(poseStack, consumer,
                    pos.getX() - cam.x, pos.getY() - cam.y, pos.getZ() - cam.z,
                    pos.getX() + 1 - cam.x, pos.getY() + 1 - cam.y, pos.getZ() + 1 - cam.z,
                    r, g, b, a);
        }
        bufferSource.endBatch(RenderType.lines());
    }

    @SuppressWarnings("unchecked")
    protected <C extends BuildContext> C context() {
        return (C) context;
    }
}