/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.build;

import net.minecraft.core.BlockPos;

public enum BuildDirection {
    UP(0, 1, 0),
    DOWN(0, -1, 0),
    NORTH(0, 0, -1),
    NORTHEAST(1, 0, -1),
    EAST(1, 0, 0),
    SOUTHEAST(1, 0, 1),
    SOUTH(0, 0, 1),
    SOUTHWEST(-1, 0, 1),
    WEST(-1, 0, 0),
    NORTHWEST(-1, 0, -1);

    private final int dx;
    private final int dy;
    private final int dz;

    BuildDirection(int dx, int dy, int dz) {
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }

    public BlockPos offset(BlockPos pos, int distance) {
        return pos.offset(dx * distance, dy * distance, dz * distance);
    }

    public boolean isVertical() {
        return this == UP || this == DOWN;
    }

    public boolean isDiagonal() {
        return dx != 0 && dz != 0;
    }

    public BuildDirection xAxis() {
        return fromOffsets(Integer.signum(dx), 0);
    }

    public BuildDirection zAxis() {
        return fromOffsets(0, Integer.signum(dz));
    }

    public BuildDirection left() {
        return fromOffsets(dz, -dx);
    }

    public BuildDirection right() {
        return fromOffsets(-dz, dx);
    }

    public BuildDirection opposite() {
        return fromOffsets(-dx, -dz);
    }

    public static BuildDirection fromYRot(float yRot) {
        switch (Math.floorMod(Math.round(yRot / 45.0F), 8)) {
            case 0:
                return SOUTH;
            case 1:
                return SOUTHWEST;
            case 2:
                return WEST;
            case 3:
                return NORTHWEST;
            case 4:
                return NORTH;
            case 5:
                return NORTHEAST;
            case 6:
                return EAST;
            default:
                return SOUTHEAST;
        }
    }

    private static BuildDirection fromOffsets(int x, int z) {
        for (BuildDirection direction : values()) {
            if (direction.dx == x && direction.dz == z) {
                return direction;
            }
        }
        throw new IllegalArgumentException("no direction for (" + x + ", " + z + ")");
    }
}
