/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.justforbuilding;

import net.justforbuilding.command.ModCommands;

public class justforbuilding {
    public static final String MOD_ID = "justforbuilding";

    public static void init() {
        ModCommands.register();
    }
}
