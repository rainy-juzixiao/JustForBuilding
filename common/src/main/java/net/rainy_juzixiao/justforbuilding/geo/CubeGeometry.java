package net.rainy_juzixiao.justforbuilding.geo;

import net.minecraft.core.BlockPos;
import net.rainy_juzixiao.justforbuilding.build.BuildDirection;
import net.rainy_juzixiao.justforbuilding.build.RectAnchor;

import java.util.List;

public class CubeGeometry {

    public static int fillPositions(BlockPos start, BuildDirection facing,
                                    int length, int width, int height,
                                    boolean frameOnly, boolean hollow,
                                    RectAnchor anchor, List<BlockPos> out) {
        BuildDirection lengthDir = anchor.lengthDir(facing);
        BuildDirection widthDir = anchor.widthDir(facing);

        if (facing.isDiagonal()) {
            lengthDir = lengthDir.xAxis();
            widthDir = widthDir.zAxis();
        }

        int count = 0;

        for (int i = 0; i < length; i++) {
            BlockPos baseX = start.offset(lengthDir.offset(BlockPos.ZERO, i));
            for (int j = 0; j < width; j++) {
                BlockPos baseXZ = baseX.offset(widthDir.offset(BlockPos.ZERO, j));
                for (int k = 0; k < height; k++) {
                    boolean include;

                    if (frameOnly) {
                        boolean onBottomEdge = (k == 0) && (i == 0 || i == length - 1 || j == 0 || j == width - 1);
                        boolean onTopEdge = (k == height - 1) && (i == 0 || i == length - 1 || j == 0 || j == width - 1);
                        boolean onVerticalEdge = (k > 0 && k < height - 1) &&
                                (i == 0 || i == length - 1) &&
                                (j == 0 || j == width - 1);
                        include = onBottomEdge || onTopEdge || onVerticalEdge;
                    } else if (hollow) {
                        include = (i == 0 || i == length - 1 ||
                                j == 0 || j == width - 1 ||
                                k == 0 || k == height - 1);
                    } else {
                        include = true;
                    }

                    if (include) {
                        out.add(baseXZ.above(k));
                        count++;
                    }
                }
            }
        }
        return count;
    }
}