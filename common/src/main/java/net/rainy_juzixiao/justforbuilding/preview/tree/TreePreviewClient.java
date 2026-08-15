/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.preview.tree;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.rainy_juzixiao.justforbuilding.build.BuildMode;
import net.rainy_juzixiao.justforbuilding.preview.PreviewFactory;
import net.rainy_juzixiao.justforbuilding.preview.Previewer;
import net.rainy_juzixiao.justforbuilding.preview.RenderPreviewer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

@Environment(EnvType.CLIENT)
public class TreePreviewClient extends RenderPreviewer {

    private static final TreePreviewClient INSTANCE = new TreePreviewClient();

    private TreePreviewClient() {
    }

    @Previewer
    public static void register() {
        PreviewFactory.register(BuildMode.TREE, INSTANCE);
    }

    @Override
    protected void computePositions(BlockPos start, Minecraft minecraft) {
        positions.add(start);
    }
}