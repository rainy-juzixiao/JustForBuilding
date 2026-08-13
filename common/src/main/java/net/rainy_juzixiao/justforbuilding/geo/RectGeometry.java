/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.geo;

import net.minecraft.core.BlockPos;
import net.rainy_juzixiao.justforbuilding.build.BuildDirection;
import net.rainy_juzixiao.justforbuilding.build.RectAnchor;

import java.util.List;

public class RectGeometry {

    public static int fillPositions(BlockPos start, BuildDirection facing, int length, int width,
                                    boolean hollow, RectAnchor anchor, List<BlockPos> out) {
        BuildDirection lengthDir = anchor.lengthDir(facing);
        BuildDirection widthDir = anchor.widthDir(facing);
        int count = 0;
        if (facing.isDiagonal()) {
            lengthDir = lengthDir.xAxis();
            widthDir = widthDir.zAxis();
        }
        for (int i = 0; i < length; i++) {
            BlockPos lengthOffset = lengthDir.offset(BlockPos.ZERO, i);
            for (int j = 0; j < width; j++) {
                if (hollow && i > 0 && i < length - 1 && j > 0 && j < width - 1) {
                    continue;
                }
                out.add(start.offset(lengthOffset).offset(widthDir.offset(BlockPos.ZERO, j)));
                count++;
            }
        }
        return count;
    }
}
