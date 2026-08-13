/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.preview.rect;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.rainy_juzixiao.justforbuilding.build.BuildDirection;
import net.rainy_juzixiao.justforbuilding.build.BuildMode;
import net.rainy_juzixiao.justforbuilding.geo.RectGeometry;
import net.rainy_juzixiao.justforbuilding.command.context.RectContext;
import net.rainy_juzixiao.justforbuilding.preview.PreviewFactory;
import net.rainy_juzixiao.justforbuilding.preview.Previewer;
import net.rainy_juzixiao.justforbuilding.preview.RenderPreviewer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

@Environment(EnvType.CLIENT)
public class RectPreviewClient extends RenderPreviewer {

    private static final RectPreviewClient INSTANCE = new RectPreviewClient();

    private RectPreviewClient() {
    }

    @Previewer
    public static void register() {
        PreviewFactory.register(BuildMode.RECT, INSTANCE);
    }

    @Override
    protected void computePositions(BlockPos start, Minecraft minecraft) {
        RectContext context = context();
        boolean boundaryOnly = context.isHollow();
        if (!boundaryOnly && context.getLength() * context.getWidth() > MAX_PREVIEW_BLOCKS) {
            boundaryOnly = true;
        }
        if (minecraft.player != null) {
            RectGeometry.fillPositions(start, BuildDirection.fromYRot(minecraft.player.yRot),
                    context.getLength(), context.getWidth(), boundaryOnly, context.getAnchor(), positions);
        }
    }
}
