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

public class SphereGeometry {

    public static int fillPositionsRadius(BlockPos center, BuildDirection facing,
                                          int radius, boolean hollow, RectAnchor anchor,
                                          List<BlockPos> out) {
        int r = Math.min(radius, 128);
        int rSq = r * r;
        int rMin = hollow ? r - 1 : 0;
        int rMinSq = rMin * rMin;
        int count = 0;

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    int distSq = x * x + y * y + z * z;
                    if (distSq <= rSq && distSq >= rMinSq) {
                        out.add(center.offset(x, y, z));
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public static int fillPositionsDiameter(BlockPos center, BuildDirection facing,
                                            int diameter, boolean hollow, RectAnchor anchor,
                                            List<BlockPos> out) {
        return fillPositionsRadius(center, facing, diameter / 2, hollow, anchor, out);
    }
}