package net.rainy_juzixiao.justforbuilding.geo;

import net.minecraft.core.BlockPos;
import net.rainy_juzixiao.justforbuilding.build.BuildDirection;
import net.rainy_juzixiao.justforbuilding.build.RectAnchor;

import java.util.List;

public class CircleGeometry {

    public static int fillPositionsRadius(BlockPos center, BuildDirection facing,
                                          int radius, boolean hollow, RectAnchor anchor,
                                          List<BlockPos> out) {
        BuildDirection xDir = anchor.lengthDir(facing);
        BuildDirection zDir = anchor.widthDir(facing);

        // 我们在这里确保它始终转换为水平轴方向
        xDir = xDir.xAxis();
        zDir = zDir.zAxis();

        int count = 0;
        int r = Math.min(radius, 256);
        int rSq = r * r;
        int rMin = hollow ? r - 1 : 0;
        int rMinSq = rMin * rMin;

        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                int distSq = x * x + z * z;
                if (distSq <= rSq && distSq >= rMinSq) {
                    BlockPos pos = center.offset(xDir.offset(BlockPos.ZERO, x))
                            .offset(zDir.offset(BlockPos.ZERO, z));
                    out.add(pos);
                    count++;
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