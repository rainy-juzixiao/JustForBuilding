package net.justforbuilding.build;

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
}
