/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.preview.sphere;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.rainy_juzixiao.justforbuilding.build.BuildDirection;
import net.rainy_juzixiao.justforbuilding.build.BuildMode;
import net.rainy_juzixiao.justforbuilding.command.context.SphereContext;
import net.rainy_juzixiao.justforbuilding.geo.SphereGeometry;
import net.rainy_juzixiao.justforbuilding.preview.PreviewFactory;
import net.rainy_juzixiao.justforbuilding.preview.Previewer;
import net.rainy_juzixiao.justforbuilding.preview.RenderPreviewer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

@Environment(EnvType.CLIENT)
public class SpherePreviewClient extends RenderPreviewer {

    private static final SpherePreviewClient INSTANCE = new SpherePreviewClient();

    private SpherePreviewClient() {
    }

    @Previewer
    public static void register() {
        PreviewFactory.register(BuildMode.SPHERE, INSTANCE);
    }

    @Override
    protected void computePositions(BlockPos start, Minecraft minecraft) {
        SphereContext context = context();
        if (context == null) {
            return;
        }

        boolean hollow = context.isHollow();
        int size = context.getSize();

        int totalBlocks = size * size * size;
        if (!hollow && totalBlocks > MAX_PREVIEW_BLOCKS) {
            hollow = true;
        }

        if (minecraft.player != null) {
            BuildDirection facing = BuildDirection.fromYRot(minecraft.player.yRot);
            if (context.isUseDiameter()) {
                SphereGeometry.fillPositionsDiameter(start, facing, size, hollow, context.getAnchor(), positions);
            } else {
                SphereGeometry.fillPositionsRadius(start, facing, size, hollow, context.getAnchor(), positions);
            }
        }
    }
}