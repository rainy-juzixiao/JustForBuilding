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
import net.rainy_juzixiao.justforbuilding.command.context.TreeContext;
import net.rainy_juzixiao.justforbuilding.preview.PreviewFactory;
import net.rainy_juzixiao.justforbuilding.preview.Previewer;
import net.rainy_juzixiao.justforbuilding.preview.RenderPreviewer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

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
        TreeContext tree = context();
        if (tree != null && tree.getShape() != null) {
            for (BlockPos offset : tree.getShape()) {
                positions.add(start.offset(offset));
            }
        } else if (tree != null && tree.getRangeHeight() > 0) {
            addRangeBox(start, tree.getRangeXZ(), tree.getRangeHeight());
        } else {
            positions.add(start);
        }
    }

    private void addRangeBox(BlockPos start, int rangeXZ, int rangeHeight) {
        List<BlockPos> corners = new ArrayList<>(8);
        for (int dx : new int[]{-rangeXZ, rangeXZ}) {
            for (int dz : new int[]{-rangeXZ, rangeXZ}) {
                corners.add(start.offset(dx, 0, dz));
                corners.add(start.offset(dx, rangeHeight, dz));
            }
        }
        for (int i = 0; i < 4; i++) { // 我们先从垂直开始添加，随后是底，然后再是顶
            addEdge(corners.get(i * 2), corners.get(i * 2 + 1));
            addEdge(corners.get(i * 2), corners.get((i + 1) % 4 * 2));
            addEdge(corners.get(i * 2 + 1), corners.get((i + 1) % 4 * 2 + 1));
        }
    }

    private void addEdge(BlockPos a, BlockPos b) {
        int dx = b.getX() - a.getX(), dy = b.getY() - a.getY(), dz = b.getZ() - a.getZ();
        int max = Math.max(Math.abs(dx), Math.max(Math.abs(dy), Math.abs(dz)));
        if (max == 0) {
            positions.add(a);
            return;
        }
        for (int m = 0; m <= max; m++) {
            double ratio = (double) m / max;
            positions.add(new BlockPos(
                    (int) Math.round(a.getX() + dx * ratio),
                    (int) Math.round(a.getY() + dy * ratio),
                    (int) Math.round(a.getZ() + dz * ratio)));
        }
    }
}
