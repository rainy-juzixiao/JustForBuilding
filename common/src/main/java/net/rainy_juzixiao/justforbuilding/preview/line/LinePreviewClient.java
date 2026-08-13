/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.preview.line;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.rainy_juzixiao.justforbuilding.build.BuildDirection;
import net.rainy_juzixiao.justforbuilding.build.BuildMode;
import net.rainy_juzixiao.justforbuilding.command.context.LineContext;
import net.rainy_juzixiao.justforbuilding.preview.PreviewFactory;
import net.rainy_juzixiao.justforbuilding.preview.Previewer;
import net.rainy_juzixiao.justforbuilding.preview.RenderPreviewer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

@Environment(EnvType.CLIENT)
public class LinePreviewClient extends RenderPreviewer {

    private static final LinePreviewClient INSTANCE = new LinePreviewClient();

    private LinePreviewClient() {
    }

    @Previewer
    public static void register() {
        PreviewFactory.register(BuildMode.PLACE, INSTANCE);
    }

    @Override
    protected void computePositions(BlockPos start, Minecraft minecraft) {
        LineContext context = context();
        int length = Math.min(context.getLength(), MAX_PREVIEW_BLOCKS);
        BuildDirection dir = context.getDirection() != null
                ? context.getDirection() : BuildDirection.fromYRot(minecraft.player.yRot);
        int step = context.getInterval() + 1;
        for (int i = 0; i < length; i += step) {
            positions.add(dir.offset(start, i));
        }
    }
}
