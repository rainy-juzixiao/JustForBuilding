/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.preview.staff;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.rainy_juzixiao.justforbuilding.build.BuildMode;
import net.rainy_juzixiao.justforbuilding.item.NBSStaffItem;
import net.rainy_juzixiao.justforbuilding.preview.PreviewFactory;
import net.rainy_juzixiao.justforbuilding.preview.Previewer;
import net.rainy_juzixiao.justforbuilding.preview.RenderPreviewer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class StaffPreviewClient extends RenderPreviewer {

    private static final StaffPreviewClient INSTANCE = new StaffPreviewClient();

    private StaffPreviewClient() {
    }

    @Previewer
    public static void register() {
        PreviewFactory.register(BuildMode.STAFF, INSTANCE);
    }

    @Override
    public void tick(Minecraft minecraft) {
        positions.clear();
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }
        if (!(minecraft.player.getMainHandItem().getItem() instanceof NBSStaffItem)) {
            return;
        }
        HitResult hit = minecraft.hitResult;
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) {
            return;
        }
        positions.add(((BlockHitResult) hit).getBlockPos());
    }

    @Override
    protected void computePositions(BlockPos start, Minecraft minecraft) {
        // 此处，tick() 已经为我们接管位置进行计算，此方法应该不会被调用
    }

    @Override
    public void render(PoseStack poseStack, Camera camera) {
        if (positions.isEmpty()) {
            return;
        }
        Vec3 cam = camera.getPosition();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());
        for (BlockPos pos : positions) {
            LevelRenderer.renderLineBox(poseStack, consumer,
                    pos.getX() - cam.x, pos.getY() - cam.y, pos.getZ() - cam.z,
                    pos.getX() + 1 - cam.x, pos.getY() + 1 - cam.y, pos.getZ() + 1 - cam.z,
                    1.0F, 0.0F, 0.0F, 0.4F);
        }
        bufferSource.endBatch(RenderType.lines());
    }
}
