/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.build;

public enum RectAnchor {
    FRONT_LEFT,
    FRONT_RIGHT,
    BACK_LEFT,
    BACK_RIGHT;

    public BuildDirection lengthDir(BuildDirection facing) {
        return isBack() ? facing.opposite() : facing;
    }

    public BuildDirection widthDir(BuildDirection facing) {
        if (isBack()) {
            return isLeft() ? facing.right() : facing.left();
        }
        return isLeft() ? facing.left() : facing.right();
    }

    private boolean isBack() {
        return this == BACK_LEFT || this == BACK_RIGHT;
    }

    private boolean isLeft() {
        return this == FRONT_LEFT || this == BACK_LEFT;
    }
}
