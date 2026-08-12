/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding;

import me.shedaniel.architectury.event.events.client.ClientTickEvent;
import net.rainy_juzixiao.justforbuilding.command.ModCommands;
import net.rainy_juzixiao.justforbuilding.preview.RectPreviewClient;
import net.rainy_juzixiao.justforbuilding.preview.RectPreviewSync;

public class justforbuilding {
    public static final String MOD_ID = "justforbuilding";

    public static void init() {
        ModCommands.register();
    }

    public static void initClient() {
        RectPreviewSync.register();
        ClientTickEvent.CLIENT_PRE.register(RectPreviewClient::tick);
    }
}
